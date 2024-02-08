package com.voltras.blockseatservice.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.admin.api.services.SegmentService;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.entities.FlightData;
import com.voltras.blockseatservice.repositories.AirportDataRepository;
import com.voltras.blockseatservice.repositories.FlightDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Service("segmentService")
public class SegmentServiceImpl implements SegmentService, RpcBasicService {
	@Autowired
	private FlightDataRepository flightDataRepo;
	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private AirportDataRepository airportDataRepo;
	@Autowired
	private LogHelper log;

	@Autowired
	private VanAdditionalRequestData session;

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<FlightSegment> getAll(String inventoryId) {
		List<FlightSegment> flightSegment = new ArrayList<>();
		var data = flightDataRepo.findByInventoryIdOrderBySegmentNumAsc(UUID.fromString(inventoryId));
		for (var segments : data) {
			var segment = new FlightSegment(segments.getSegmentDetails().segmentId(),
					segments.getSegmentDetails().isDepart(), segments.getSegmentDetails().departure(),
					segments.getSegmentDetails().arrival(), segments.getSegmentDetails().flightDuration(),
					segments.getSegmentDetails().operatingAirline(), segments.getSegmentDetails().flightNumber(),
					segments.getSegmentDetails().stop(), segments.getSegmentDetails().equipment(),
					segments.getSegmentDetails().transitDuration());
			flightSegment.add(segment);
		}
		return flightSegment;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<FlightSegment> createSegment(String inventoryId, FlightSegment flightSegment) {
		var inven = inventoryRepo.findByInventoryId(UUID.fromString(inventoryId)).orElse(null);
		if (inven == null) {
			log.error("[{}.createSegment] Data inventory tidak ditemukan", this.getClass().getCanonicalName());
			throw new RuntimeException();
		}

		FlightData lastFlightData = flightDataRepo.findTopByInventoryIdOrderBySegmentNumDesc(inven.getInventoryId())
				.orElse(null);

		List<FlightSegment> segmentResult = new ArrayList<>();
		var createdBy = session.getUser().getPrincipal();
		var createdAt = new Date();
		var departIata = flightSegment.departure().iata();
		var arrivalIata = flightSegment.arrival().iata();
		var zoneDepart = airportDataRepo.getTimezonefindByIataCode(departIata);
		var zoneArrival = airportDataRepo.getTimezonefindByIataCode(arrivalIata);
		var flightDuration = Duration.between(flightSegment.departure().time().minusHours(zoneDepart),
				flightSegment.arrival().time().minusHours(zoneArrival));
		Integer flightDurationInMinutes = (int) flightDuration.toMinutes();
		Integer transitDuration = lastFlightData != null
				&& lastFlightData.getSegmentDetails().isDepart().equals(flightSegment.isDepart())
						? (int) Duration.between(lastFlightData.getSegmentDetails().arrival().time(),
								flightSegment.departure().time()).toMinutes()
						: 0;

		var segment = new FlightSegment(UUID.randomUUID().toString(), flightSegment.isDepart(),
				flightSegment.departure(), flightSegment.arrival(), flightDurationInMinutes,
				flightSegment.operatingAirline(), flightSegment.flightNumber(), flightSegment.stop(),
				flightSegment.equipment(), transitDuration);
		segmentResult.add(segment);

		Integer nextSegmentNum = lastFlightData != null ? lastFlightData.getSegmentNum() + 1 : 0;
		var flightData = new FlightData(UUID.randomUUID(), inven.getInventoryId(), segment.segmentId(), nextSegmentNum,
				createdBy, createdAt, null, null, segment);
		flightDataRepo.saveAndFlush(flightData);
		log.info("[{}.createSegment] Data segment berhasil dibuat", this.getClass().getCanonicalName());
		return segmentResult;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<FlightSegment> editSegment(FlightSegment flightSegment) {
		List<FlightSegment> segmentResult = new ArrayList<>();
		var updateBy = session.getUser().getPrincipal();
		var updatedAt = new Date();

		var data = flightDataRepo.findBySegmentId(flightSegment.segmentId());
		FlightData lastFlightData = data.getSegmentNum().equals(0) ? null
				: flightDataRepo.findByInventoryIdAndSegmentNum(data.getInventoryId(), data.getSegmentNum() - 1);
		var departIata = flightSegment.departure().iata();
		var arrivalIata = flightSegment.arrival().iata();
		var zoneDepart = airportDataRepo.getTimezonefindByIataCode(departIata);
		var zoneArrival = airportDataRepo.getTimezonefindByIataCode(arrivalIata);
		var flightDuration = Duration.between(flightSegment.departure().time().minusHours(zoneDepart),
				flightSegment.arrival().time().minusHours(zoneArrival));
		Integer flightDurationInMinutes = (int) flightDuration.toMinutes();
		Integer transitDuration = lastFlightData != null
				&& lastFlightData.getSegmentDetails().isDepart().equals(flightSegment.isDepart())
						? (int) Duration.between(lastFlightData.getSegmentDetails().arrival().time(),
								flightSegment.departure().time()).toMinutes()
						: 0;

		var segment = new FlightSegment(flightSegment.segmentId(), flightSegment.isDepart(), flightSegment.departure(),
				flightSegment.arrival(), flightDurationInMinutes, flightSegment.operatingAirline(),
				flightSegment.flightNumber(), flightSegment.stop(), flightSegment.equipment(), transitDuration);
		segmentResult.add(segment);
		if (!data.getSegmentId().equals(segment.segmentId())) {
			log.error("[{}.editSegment] Data segment tidak ditemukan", this.getClass().getCanonicalName());
			throw new RuntimeException();
		} else {
			var flightDatas = new FlightData(data.getId(), data.getInventoryId(), data.getSegmentId(),
					data.getSegmentNum(), data.getCreatedBy(), data.getCreatedAt(), updateBy, updatedAt, segment);
			flightDataRepo.saveAndFlush(flightDatas);
		}
		return segmentResult;
	}
}

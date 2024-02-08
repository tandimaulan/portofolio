package com.voltras.blockseatservice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.Fare;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;
import com.voltras.blockseat.api.models.JourneyOption;
import com.voltras.blockseat.api.models.Supplier;
import com.voltras.blockseat.api.services.BlockseatAvailabilityService;
import com.voltras.blockseatservice.components.AvailabiltyHelper;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

@Service("blockseatAvailabilityService")
public class AvailabilityServiceImpl implements BlockseatAvailabilityService, RpcBasicService {

	@Autowired
	private InventoryRepository inventoryRepo;

	@Autowired
	private AvailabiltyHelper availHelper;
	@Autowired
	LogHelper log;

	@Override
	@Publish(allowAll = true)
	public List<JourneyOption> search(String param, LocalDate availableFrom, LocalDate availableTo,
			CabinClass cabinClass) {
		List<JourneyOption> searchResult = new ArrayList<>();
		if (param != null) {
			var avail = inventoryRepo.findBySearchTagAndCabinClass(param, cabinClass);
			JourneyOption journeyOptions = null;
			List<Fare> fareData = new ArrayList<Fare>();
			for (var avails : avail) {
				if (avails.getIsAvailable().equals(true)) {

					if (avails.getSeatAllot() < avails.getMinSeatBooking()) {
						avails.setIsAvailable(false);
						inventoryRepo.saveAndFlush(avails);
					}

					var flightData = avails.getFlightData();
					var flightDate = flightData.stream()
							.map(fd -> fd.getSegmentDetails().departure().time().toLocalDate())
							.sorted(Comparator.naturalOrder()).findFirst().get();

					if (flightDate.isAfter(availableTo) || flightDate.isBefore(availableFrom)) {

					} else {

						List<FlightSegment> flightSegment = availHelper.getFlightSegement(avails.getInventoryId());

						FlightSummary flightSummary = availHelper.getFlightSummary(avails.getInventoryId());

						List<String> note = Arrays.asList(avails.getTitle(), avails.getNotes());
						fareData = avails.getFareData().stream().map(farezs -> new Fare(farezs.getSubClass(),
								farezs.getPrice(), farezs.getAllotment(), farezs.getIsActive()))
								.collect(Collectors.toList());
						var seatCapacity = avails.getFareData().stream().mapToInt(sc -> sc.getAllotment()).sum();

						List<Supplier> supplierData = avails.getSupplierData().stream()
								.map(sd -> new Supplier(sd.getId(), sd.getSupplierName(), sd.getSupplierEmail()))
								.collect(Collectors.toList());

						journeyOptions = new JourneyOption(avails.getInventoryId().toString(), avails.getAirlineName(),
								seatCapacity, fareData, avails.getMinSeatBooking(), avails.getCabinClass(),
								flightSummary, flightSegment, note, avails.getSearchTag(), avails.getDownPaymentPrice(),
								supplierData, avails.getIsAvailable());
						if (!avails.getTimelimitData().isEmpty() && !flightSegment.isEmpty() && !fareData.isEmpty()
								&& !supplierData.isEmpty()) {
							searchResult.add(journeyOptions);
						}

					}

				} else {

				}
			}
		} else {

			var avail = inventoryRepo.findByCabinClass(cabinClass);
			JourneyOption journeyOptions = null;
			List<Fare> fareData = new ArrayList<Fare>();
			for (var avails : avail) {
				if (!avails.getIsAvailable()) {
					continue;
				}
				var flightData = avails.getFlightData();
				if (avails.getSeatAllot() < avails.getMinSeatBooking()) {
					avails.setIsAvailable(false);
					inventoryRepo.saveAndFlush(avails);
				}

				var flightDate = flightData.stream().map(fd -> fd.getSegmentDetails().departure().time().toLocalDate())
						.sorted(Comparator.naturalOrder()).findFirst().get();

				if (flightDate.isAfter(availableTo) || flightDate.isBefore(availableFrom)) {

				} else {
					List<FlightSegment> flightSegment = availHelper.getFlightSegement(avails.getInventoryId());

					FlightSummary flightSummary = availHelper.getFlightSummary(avails.getInventoryId());

					List<String> note = Arrays.asList(avails.getTitle(), avails.getNotes());
					fareData = avails.getFareData().stream().map(farezs -> new Fare(farezs.getSubClass(),
							farezs.getPrice(), farezs.getAllotment(), farezs.getIsActive()))
							.collect(Collectors.toList());
					var seatCapacity = avails.getFareData().stream().mapToInt(sc -> sc.getAllotment()).sum();
					avails.setSeatAllot(seatCapacity);

					List<Supplier> supplierData = avails.getSupplierData().stream()
							.map(sd -> new Supplier(sd.getId(), sd.getSupplierName(), sd.getSupplierEmail()))
							.collect(Collectors.toList());

					journeyOptions = new JourneyOption(avails.getInventoryId().toString(), avails.getAirlineName(),
							seatCapacity, fareData, avails.getMinSeatBooking(), avails.getCabinClass(), flightSummary,
							flightSegment, note, avails.getSearchTag(), avails.getDownPaymentPrice(), supplierData,
							avails.getIsAvailable());
					if (!avails.getTimelimitData().isEmpty() && !flightSegment.isEmpty() && !fareData.isEmpty()
							&& !supplierData.isEmpty()) {
						searchResult.add(journeyOptions);
					}

				}
			}
		}
		Comparator<JourneyOption> comparator = Comparator.comparing(journeyOption -> {
			var segments = journeyOption.segmentDetails();
			return segments.stream().map(segment -> segment.departure().time()).min(LocalDateTime::compareTo)
					.orElse(LocalDateTime.MIN);
		});

		List<JourneyOption> sortedResult = searchResult.stream().filter(journeyOption -> {
			LocalDate firstSegmentDeparture = journeyOption.segmentDetails().stream().findFirst()
					.map(segment -> segment.departure().time().toLocalDate()).orElse(null);

			return firstSegmentDeparture != null && (firstSegmentDeparture.isEqual(availableFrom)
					|| firstSegmentDeparture.isEqual(availableTo)
					|| (firstSegmentDeparture.isAfter(availableFrom) && firstSegmentDeparture.isBefore(availableTo)));
		}).sorted(comparator).collect(Collectors.toList());

		return sortedResult;

	}
}

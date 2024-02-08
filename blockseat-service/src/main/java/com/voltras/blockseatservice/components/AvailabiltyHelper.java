package com.voltras.blockseatservice.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.voltras.blockseat.api.models.Airport;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;
import com.voltras.blockseatservice.repositories.FlightDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;

@Component
public class AvailabiltyHelper {

	@Autowired
	private InventoryRepository inventoryRepo;

	@Autowired
	private FlightDataRepository flightDataRepo;

	public List<FlightSegment> getFlightSegement(UUID inventoryId) {
		List<FlightSegment> flightSegment = new ArrayList<>();

		var segmentData = flightDataRepo.findByInventoryIdOrderBySegmentNumAsc(inventoryId);
		for (var flightSegments : segmentData) {
			var segmentDetails = flightSegments.getSegmentDetails();
			var newSegment = new FlightSegment(segmentDetails.segmentId(), segmentDetails.isDepart(),
					segmentDetails.departure(), segmentDetails.arrival(), segmentDetails.flightDuration(),
					segmentDetails.operatingAirline(), segmentDetails.flightNumber(), segmentDetails.stop(),
					segmentDetails.equipment(), segmentDetails.transitDuration());
			flightSegment.add(newSegment);
		}
		return flightSegment;
	}

	public FlightSummary getFlightSummary(UUID inventoryId) {
		FlightSummary summary = null;
		var inventoryData = inventoryRepo.getByInventoryId(inventoryId);
		var segmentData = flightDataRepo.findByInventoryIdOrderBySegmentNumAsc(inventoryId);
		for (var segments : segmentData) {
			List<FlightSegment> segment = getFlightSegement(segments.getInventoryId());
			List<Airport> transitPoints = new ArrayList<>();
			if (!segments.getSegmentNum().equals(0)) {
				var airport = new Airport(segments.getSegmentDetails().departure().iata(),
						segments.getSegmentDetails().departure().name(),
						segments.getSegmentDetails().departure().city(),
						segments.getSegmentDetails().departure().country());
				transitPoints.add(airport);
			}
			List<String> operatingAirline = new ArrayList<>();
			for (var opAirline : segment) {
				operatingAirline.add(opAirline.operatingAirline());
			}
			operatingAirline = operatingAirline.stream().distinct().collect(Collectors.toList());

			var travelTime = segment.stream()
					.mapToInt(time -> Integer.sum(time.flightDuration(), time.transitDuration())).sum();

			var stop = 0;
			stop = stop + segments.getSegmentDetails().stop();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			summary = new FlightSummary(segmentData.get(0).getSegmentDetails().departure(),
					segments.getSegmentDetails().arrival(), travelTime, operatingAirline,
					stop + inventoryData.getFlightData().size() - 1, transitPoints);
		}
		return summary;
	}
}

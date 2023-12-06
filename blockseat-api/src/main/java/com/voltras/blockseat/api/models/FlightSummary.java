package com.voltras.blockseat.api.models;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;

@Validated
public record FlightSummary(@Valid LocationInfo departure, @Valid LocationInfo arrival,
		@NotNull @Positive Integer travelTime, @NotEmpty List<String> operatingAirlines,
		@NotNull @PositiveOrZero Integer stop, List<Airport> transitPoints) {
}
/**
 * @param departure				departure data for flight segment
 * @param arrival				arrival data for flight segment
 * @param travelTime			time travel
 * @param opertatingAirlines	list aircraft operating
 * @param stop					total flight has transit
 * @param transitPoints			List data airport flight has transit				
 */
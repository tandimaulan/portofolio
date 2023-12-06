package com.voltras.blockseat.api.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;

@Validated
public record FlightSegment(String segmentId, @NotNull Boolean isDepart, @Valid LocationInfo departure,
		@Valid LocationInfo arrival, Integer flightDuration, @NotBlank String operatingAirline,
		@NotBlank String flightNumber, @NotNull @PositiveOrZero Integer stop, String equipment,
		Integer transitDuration) {
}

/**
 * @param segmentId         id for flight segment
 * @param isDepart          if true then early departure and if not not early
 *                          departure
 * @param departure         departure data for flight segment
 * @param arrival           arrival data for flight segment
 * @param flightDuration    time during flight in minute
 * @param opertatingAirline aircraft operating
 * @param flightNumber      aircraft flight number
 * @param stop              if the flight has transit
 * @param equipment         aircraft code
 * @param transitDuration   transit duration in minute
 */
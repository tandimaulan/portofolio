package com.voltras.blockseat.api.models;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;

import com.voltras.blockseat.api.enums.CabinClass;

@Validated
public record JourneyOption(@NotBlank String inventoryId, @NotNull String airlineName,
		@NotNull @PositiveOrZero Integer seatCapacity, @NotNull List<Fare> fare,
		@NotNull @Positive Integer minSeatBooking, @NotNull CabinClass cabinClass, FlightSummary segmentSummary,
		List<FlightSegment> segmentDetails, @NotNull List<String> notes, @NotNull String tags, Double downPaymentPrice,
		List<Supplier> supplier, Boolean isAvailable) {
}
/**
 * @param inventoryId		id for journey
 * @param airlineName		airline name in journey
 * @param seatCapacity		seat capacity in journey
 * @param fare				list fare data for journey
 * @param minSeatBooking	minimal seat for order
 * @param cabinClass		cabin class in airline
 * @param segmentSummary	flight summary in journey
 * @param segmentDetails	flight segment in journey
 * @param notes				notes for journey
 * @param tags				tags journey for method search in BlockseatAvailabilityService
 * @param downPaymentPrice	down payment for journey
 * @param supplier			supplier journey
 * @param isAvailable		data is available or not available
 */
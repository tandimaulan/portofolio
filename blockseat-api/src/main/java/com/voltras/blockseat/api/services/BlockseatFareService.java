package com.voltras.blockseat.api.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;

import com.voltras.blockseat.api.exceptions.SeatNotAvailableException;
import com.voltras.blockseat.api.models.FareDetail;

@Validated
public interface BlockseatFareService {
	/**
	 * @param inventoryId       Id from Journey
	 * @param paxCount    		total pax for order
	 * @param subClass      	subClass for order
	 * 
	 * @return {@link FareDetail} calculateTotalPrice for order blockseat
	 * 
	 */
	FareDetail calculateTotalPrice(@NotBlank String inventoryId, @NotNull @Positive Integer paxCount,
			@NotNull String subClass) throws SeatNotAvailableException;
}
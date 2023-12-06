package com.voltras.blockseat.admin.api.models;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;

@Validated
public record FareDetail(
		@NotNull @Positive UUID fareId,
		@NotNull @Positive String subClass,
		@NotNull @Positive Double price,
		@NotNull @Positive Integer allotment,
		Integer downPayment,
		Integer totalPaidOff,
		@NotNull @Positive Boolean isActive) {
}
/**
 * @param fareId		unique flag for fare
 * @param subClass		subClass for fare
 * @param price			price for Journey
 * @param allotment		seat allotment for Journey
 * @param downPayment 	total for total down payment 
 * @param totalPaidOff	total for total paid off
 * @param isActive		active or not active fare
 */

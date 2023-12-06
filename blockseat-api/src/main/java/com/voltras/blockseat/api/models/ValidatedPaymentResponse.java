package com.voltras.blockseat.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public record ValidatedPaymentResponse(@NotBlank String bookingCode, @NotNull Double price, Double totalPayment,
		Contact contact) {

}
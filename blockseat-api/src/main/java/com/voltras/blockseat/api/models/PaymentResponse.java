package com.voltras.blockseat.api.models;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public record PaymentResponse(@NotBlank String item, @NotBlank String bookingCode, String url) {

}

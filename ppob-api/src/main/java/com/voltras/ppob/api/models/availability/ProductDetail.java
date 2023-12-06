package com.voltras.ppob.api.models.availability;

import org.springframework.validation.annotation.Validated;

import com.voltras.ppob.api.models.ProductCode;

import jakarta.validation.constraints.NotBlank;

@Validated
public record ProductDetail(@NotBlank String code, @NotBlank ProductCode productCode, Double nominal, String voucherName,
		String description) {

}
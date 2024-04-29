package com.voltras.helpdesk.api.models;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
public record Category(@NotNull String name, @NotNull Boolean needBody, @NotNull Boolean needBookingCode,
		@NotNull Boolean needBankName, @NotNull Boolean needBankCode, @NotNull Boolean needAccountName,
		@NotNull Boolean needAccountNumber, @NotNull Boolean needBranchBank, @NotNull Boolean needPhoneNumber) {
}
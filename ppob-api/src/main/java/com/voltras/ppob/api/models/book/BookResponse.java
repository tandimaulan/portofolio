package com.voltras.ppob.api.models.book;

import java.util.Date;
import java.util.UUID;

import com.voltras.core.common.api.enums.BookingStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookResponse(@NotBlank UUID bookId, @NotBlank String bookingCode, @NotNull @Valid BookingStatus status,
		Date bookingDate, Date timelimit, String customerName, Fare fare, Double totalPrice,
		String virtualAccountNumber, String referenceNumber) {

}
/**
 * @param bookingCode          Booking code of this transaction
 * @param status               Booking status of this transaction
 * @param bookingDate          Date of transaction
 * @param timelimit            Time limit for this transaction
 * @param customerName         Name of customer
 * @param fare                 Fare info for transaction
 * @param totalPrice           Total price for payment
 * @param virtualAccountNumber VA for bill BPJS
 */
package com.voltras.ppob.api.models.book;

import java.util.Date;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.ppob.api.models.availability.ProductDetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionData(@NotBlank ProductDetail productDetail, @NotBlank String bookingCode,
		@NotNull Double totalPrice, @NotNull BookingStatus status, @NotNull Date bookingDate, String customerName,
		String customerNumber, String token, String referenceNumber, String serialNumber, String virtualAccountNumber) {

}
/**
 * @param productDetail   Booking code of this transaction
 * @param bookingCode     Booking status of this transaction
 * @param status          Time limit for this transaction
 * @param bookingDate     Time limit for this transaction
 * @param token           token for PLN-PREPAID
 * @param referenceNumber reference number from gateway
 * @param serialNumber    serial number from gateway for voucher/pulsa
 */

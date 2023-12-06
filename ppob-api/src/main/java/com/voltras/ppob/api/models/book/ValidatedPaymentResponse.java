package com.voltras.ppob.api.models.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidatedPaymentResponse(@NotBlank String bookingCode, @NotNull Double totalPrice, Double totalPayment, String productCode) {

}

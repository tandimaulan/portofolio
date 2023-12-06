package com.voltras.ppob.api.models.book;

import jakarta.validation.constraints.NotBlank;

public record PaymentResponse(@NotBlank String item, @NotBlank String bookingCode, String url) {

}
/**
 * @param bookingCode                Booking code of this transaction
 * @param status                     Booking status of this transaction
 * @param timelimit                  Time limit for this transaction
 */

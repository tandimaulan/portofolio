package com.voltras.blockseat.api.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;

@Validated
public record FareDetail(@NotNull @Positive Double price, @NotNull @Positive Double totalPrice,
		@NotNull @Positive Double downPaymentPrice, @NotNull @Positive Double totalDownPaymentPrice) {
}
/**
 * @param price 						price of journey
 * @param totalPrice 					the total price to be paid when the customer has placed an order.
 * @param downPaymentPrice				down payment to be paid when the customer has placed an order.
 * @param totalDownPaymentPrice			the total Down Payment Price
 */
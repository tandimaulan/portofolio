package com.voltras.blockseat.api.models;

import java.time.LocalDateTime;

import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;

public record Statement(Integer seatBooked, Double pricePerSeat, Double totalPrice, Double totalPayment,
		LocalDateTime paymentDate, PaymentStatus paymentStatus, PaymentType paymentType) {

}
/**
 *@param seatBooked			seat booking
 *@param pricePerSeat		price per seat
 *@param totalPrice			total price for payment
 *@param totalPayment		total payment for payment
 *@param paymentDate		date for payment
 *@param paymentStatus		status for payment
 *@param paymentType 		type for payment
 */
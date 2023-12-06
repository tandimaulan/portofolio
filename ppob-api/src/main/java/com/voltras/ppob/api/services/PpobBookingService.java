package com.voltras.ppob.api.services;

import java.util.List;

import com.voltras.core.common.api.exceptions.GatewayTimeoutException;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.common.api.exceptions.InsufficientBalanceException;
import com.voltras.ppob.api.models.availability.ProductDetail;
import com.voltras.ppob.api.models.book.BookResponse;
import com.voltras.ppob.api.models.book.PaymentResponse;
import com.voltras.ppob.api.models.book.RetrieveFilter;
import com.voltras.ppob.api.models.book.TransactionData;
import com.voltras.ppob.api.models.book.ValidatedPaymentResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface PpobBookingService {

	/**
	 * @param productDetail productDetail for booking
	 * @param number        number for PLN, Voucher, BPJS, PDAM
	 * @param detail        condition for PLN-POSTPAID, PDAM. true for show response
	 *                      detail
	 * 
	 * @throws GatewayTimeoutException Thrown if there is a problem connecting to
	 *                                 gateway
	 * @return {@link book} book Booking
	 * 
	 * @implNote sesuaikan number dengan productDetail yang akan di booking
	 */
	BookResponse book(ProductDetail productDetail, String number, Boolean detail) throws GatewayTimeoutException;

	/**
	 * @param bookingCode Code for order get from method book
	 * @param page        Index of requested page
	 * @param size        Max item for each page
	 * @param filter      filter for transaction data; refer to:
	 *                    {@link RetrieveFilter}
	 * 
	 * @return List of {@link TransactionData}
	 * 
	 */
	List<TransactionData> retrieveTransaction(String bookingCode, Integer page, Integer size, RetrieveFilter filter);

	/**
	 * @param bookingCode  Code for order get from method book
	 * @param paymentType  Payment type for payment
	 * @param totalPayment total payment for payment
	 * 
	 * @return {@link ValidatedPaymentResponse} validatePayment validate payment
	 *         before finalize payment
	 * 
	 */
	ValidatedPaymentResponse validatedPayment(@NotBlank String bookingCode, @NotNull @Valid PaymentType paymentType,
			Double totalPayment) throws InsufficientBalanceException;

	/**
	 * @param bookingCode           Booking code from booking data
	 * @param paymentStatus         Payment Status parameter for finalizeDownPayment
	 * @param paymentType           Payment Type parameter for finalizeDownPayment
	 * @param totalPayment          Total Payment parameter for finalizeDownPayment
	 * @param goblinAccountId       Goblin Account Id parameter for
	 *                              finalizeDownPayment
	 * @param providerTransactionId Provider Transaction Id parameter for
	 *                              finalizeDownPayment
	 * @param transactionId         Transaction Id parameter for finalizeDownPayment
	 * 
	 * @return {@link PaymentResponse} finalizePayment for Finalize Payment
	 * 
	 */
	PaymentResponse finalizePayment(@NotBlank String bookingCode, @NotNull @Valid PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId,
			@NotNull @Valid PaymentStatus paymentStatus);

	/**
	 * @param bookingCode  code for order
	 * @param paymentType  payment type for order
	 * @param totalPayment total payment for order
	 * 
	 * @return {@link PaymentResponse} payment order
	 * 
	 */
	PaymentResponse payment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException;

}

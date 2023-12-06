package com.voltras.blockseat.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.enums.DocumentType;
import com.voltras.blockseat.api.exceptions.AmountDoesntMatchException;
import com.voltras.blockseat.api.exceptions.IncompletePassengerDataException;
import com.voltras.blockseat.api.exceptions.SeatNotAvailableException;
import com.voltras.blockseat.api.exceptions.TimelimitExpiredException;
import com.voltras.blockseat.api.models.Contact;
import com.voltras.blockseat.api.models.PaymentResponse;
import com.voltras.blockseat.api.models.Person;
import com.voltras.blockseat.api.models.Pnr;
import com.voltras.blockseat.api.models.ValidatedPaymentResponse;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.common.api.exceptions.InsufficientBalanceException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
public interface BlockseatPnrService {

	/**
	 * @param journeyOptId Journey Id for order
	 * @param paxCount     Total pax for order
	 * @param contact      Contact for order
	 * @param subClass     subClass for order
	 * 
	 * @return {@link book} book Booking
	 * 
	 */
	Pnr book(UUID journeyOptId, Integer paxCount, @Valid Contact contact, CabinClass cabinClass, String subClass)
			throws SeatNotAvailableException;

	/**
	 * @param bookingCode  Code for order get from method book
	 * @param paymentType  Payment type for payment
	 * @param totalPayment total payment for payment
	 * 
	 * @return {@link ValidatedPaymentResponse} validatePayment validate payment
	 *         before finalize payment
	 * 
	 */
	ValidatedPaymentResponse validatePayment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException,
			IncompletePassengerDataException;

	/**
	 * @param bookingCode 		 		Booking code from booking data
	 * @param paymentStatus     		Payment Status parameter for finalizeDownPayment
	 * @param paymentType      			Payment Type parameter for finalizeDownPayment
	 * @param totalPayment     			Total Payment parameter for finalizeDownPayment
	 * @param goblinAccountId     		Goblin Account Id parameter for finalizeDownPayment
	 * @param providerTransactionId     Provider Transaction Id parameter for finalizeDownPayment
	 * @param transactionId     		Transaction Id parameter for finalizeDownPayment
	 * 
	 * @return {@link PaymentResponse} finalizePayment for Finalize Payment
	 * 
	 */
	PaymentResponse finalizePayment(String bookingCode, PaymentStatus paymentStatus, PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId)
			throws InsufficientBalanceException, AmountDoesntMatchException;

	/**
	 * @param bookingCode 		Journey Id for order
	 * @param paymentType     	Total pax for order
	 * @param totalPayment    	Contact for order
	 * 
	 * @return {@link PaymentResponse} payment order
	 * 
	 */
	PaymentResponse payment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException;

	/**
	 * @param bookingCode  		Code for order get from method book
	 * @param paymentType  		Payment type for payment
	 * @param totalPayment 		total payment for payment
	 * 
	 * @return {@link ValidatedPaymentResponse} validateDownPayment validate down
	 *         payment before finalize down payment
	 * 
	 */
	ValidatedPaymentResponse validateDownPayment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException;

	/**
	 * @param bookingCode 		 		Booking code from booking data
	 * @param paymentStatus     		Payment Status parameter for finalizeDownPayment
	 * @param paymentType      			Payment Type parameter for finalizeDownPayment
	 * @param totalPayment     			Total Payment parameter for finalizeDownPayment
	 * @param goblinAccountId     		Goblin Account Id parameter for finalizeDownPayment
	 * @param providerTransactionId     Provider Transaction Id parameter for finalizeDownPayment
	 * @param transactionId     		Transaction Id parameter for finalizeDownPayment
	 * 
	 * @return {@link PaymentResponse} finalizeDownPayment for Finalize Down Payment
	 * 
	 */
	PaymentResponse finalizeDownPayment(String bookingCode, PaymentStatus paymentStatus, PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId)
			throws InsufficientBalanceException, AmountDoesntMatchException;

	/**
	 * @param bookingCode 			Booking code from booking data
	 * @param paymentType     		Payment type for payment
	 * @param totalPayment      	Total payment for payment
	 * 
	 * @return {@link PaymentResponse} downPayment for down payment order
	 * 
	 */
	PaymentResponse downPayment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException;

	/**
	 * @param internalBookingCode   Booking code from booking data
	 * 
	 * @return {@link List} <{@link Pnr> retrieve for booking data
	 * 
	 */
	List<Pnr> retrieve(String internalBookingCode) throws DataNotFoundException;

	/**
	 * @param internalBookingCode 	Booking code from booking data
	 * @param passengers     		List person data for booking data
	 * 
	 * @return {@link List} <{@link Pnr> addPassengers for booking data
	 * 
	 */
	List<Pnr> addPassengers(@NotBlank String internalBookingCode, List<Person> passengers);

	/**
	 * @param bookingCode 	Booking code for print receipt
	 * @param type     		Document type for print
	 * @param shareToEmail  if true send to email and false not send to email
	 * 
	 * @return {@link String} print for receipt
	 * 
	 */
	String print(@NotBlank String bookingCode, @NotNull DocumentType type, @NotNull Boolean shareToEmail)
			throws DataNotFoundException;
}
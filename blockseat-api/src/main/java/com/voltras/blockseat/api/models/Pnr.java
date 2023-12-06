package com.voltras.blockseat.api.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.core.common.api.enums.BookingStatus;

public record Pnr(
		UUID id, 
		String internalBookingCode, 
		List<Statement> statements, 
		String notes, 
		Double totalPrice,
		Double outstanding, 
		Double downPaymentPrice,
		Integer seatCount, 
		BookingStatus status, 
		LocalDateTime timelimit,
		String timelimitDescription, 
		List<String> remarks, 
		CabinClass cabinClass, 
		String subClass, 
		Contact contact,
		FlightSummary flightSummary, 
		List<FlightSegment> flightSegments, 
		LocalDateTime bookDate, 
		Boolean isAvailable,
		List<Person> passengers, 
		Double totalPayment,
		String bookedBy) {

}
/**
 * @param id 						unique id in pnr
 * @param internalBookingCode		booking code for customer
 * @param statements				list of payment data
 * @param notes						notes of journey
 * @param totalPrice				total price that must be paid
 * @param outstanding				outstanding that must be paid
 * @param downPaymentPrice			down payment must be paid
 * @param seatCount					total seat of booking
 * @param status					status booking
 * @param timelimit 				the deadline to pay the transaction 
 * @param timelimitDescription		description the deadline to pay the transaction
 * @param remarks					list notes if have changes in journey
 * @param subClass					the ordered subClass
 * @param contact					the ordered contact
 * @param flightSummary				flight summary journey
 * @param flightSegments			flight segment journey	
 * @param bookDate					the ordered date
 * @param isAvailable				
 * @param passengers				the ordered passengers data
 * @param totalPayment				total payment that has been paid
 * @param bookedBy					ordered by agent van
 */
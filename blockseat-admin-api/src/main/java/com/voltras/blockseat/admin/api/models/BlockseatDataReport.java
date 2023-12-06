package com.voltras.blockseat.admin.api.models;

import java.time.LocalDateTime;

public record BlockseatDataReport(
		String supplierEmail, 
		String journeyTitle, 
		Integer duration, 
		String route,
		LocalDateTime departureDate, 
		LocalDateTime arrivalDate, 
		Integer seatCapacity, 
		Integer statusNew,
		Integer statusBooked, 
		Integer statusConfirm,
		Integer totalStatusNew,
		Integer totalstatusBooked, 
		Integer totalstatusConfirm,
		Double totalPayment,
		Double totalRevenue,
		Integer totalSupplier,
		Integer totalAllSeatCapacity,
		Integer seatOpen,
		Integer totalSeatOpenActive,
		Integer totalSeatOpenInActive) {

}
/**
 *@param supplierEmail				email supplier who has the journey
 *@param journeyTitle				title from journey
 *@param duration					duration from journey
 *@param route						route journey
 *@param departureDate				first departure date from journey
 *@param arrivalDate				end arrival date from journey
 *@param seatCapacity				seatCapacity from journey
 *@param statusNew					total new status ordering per journey
 *@param statusBooked				total booked status ordering per journey
 *@param statusConfirm				total confirm status ordering per journey
 *@param totalStatusNew				total new status ordering all journey
 *@param totalstatusBooked			total booked status ordering all journey
 *@param totalstatusConfirm			total confirm status ordering all journey
 *@param totalPayment				total payment per journey
 *@param totalRevenue				total revenue from selling all journey
 *@param totalSupplier				total supplier that have journey
 *@param totalAllSeatCapacity		total seat capacity all journey
 *@param seatOpen					seat open per journey
 *@param totalSeatOpenActive		total seat open all active journey
 *@param totalSeatOpenInActive		total seat open all inactive journey
 */

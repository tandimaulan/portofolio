package com.voltras.blockseatservice.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DataReport {
	@Id
	@GeneratedValue
	UUID id;

	String supplierEmail;
	String journeyTitle;
	Integer duration;
	String route;
	LocalDateTime departureDate;
	LocalDateTime arrivalDate;
	Integer seatCapacity;
	Integer statusNew;
	Integer statusBooked;
	Integer statusConfirm;
	Integer totalStatusNew;
	Integer totalstatusBooked;
	Integer totalstatusConfirm;
	Double totalPayment;
	Double totalRevenue;
	Integer totalSupplier;
	Integer totalAllSeatCapacity;
	Integer seatOpen;

	public DataReport(String supplierEmail, String journeyTitle, Integer duration, String route,
			LocalDateTime departureDate, LocalDateTime arrivalDate, Integer seatCapacity, Integer statusNew,
			Integer statusBooked, Integer statusConfirm, Integer totalStatusNew, Integer totalstatusBooked,
			Integer totalstatusConfirm, Double totalPayment, Double totalRevenue, Integer totalSupplier,
			Integer totalAllSeatCapacity, Integer seatOpen) {
		super();
		this.supplierEmail = supplierEmail;
		this.journeyTitle = journeyTitle;
		this.duration = duration;
		this.route = route;
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.seatCapacity = seatCapacity;
		this.statusNew = statusNew;
		this.statusBooked = statusBooked;
		this.statusConfirm = statusConfirm;
		this.totalStatusNew = totalStatusNew;
		this.totalstatusBooked = totalstatusBooked;
		this.totalstatusConfirm = totalstatusConfirm;
		this.totalPayment = totalPayment;
		this.totalRevenue = totalRevenue;
		this.totalSupplier = totalSupplier;
		this.totalAllSeatCapacity = totalAllSeatCapacity;
		this.seatOpen = seatOpen;
	}

}

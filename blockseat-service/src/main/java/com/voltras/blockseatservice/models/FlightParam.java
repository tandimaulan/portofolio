package com.voltras.blockseatservice.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FlightParam {
	private String airlineLogo;
	private String arrivalAirport;
	private String arrivalCity;
	private LocalDateTime arrivalTime;
	private String departAirport;
	private String departCity;
	private LocalDateTime departTime;
	private String flightClazz;
	private String flightDuration;
	private String flightEquipment;
	private String flightName;
	private String flightNum;
	private String transitDuration;
	private String backColor;
}
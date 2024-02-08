package com.voltras.blockseatservice.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = { @Index(columnList = "iataCode", unique = true) })
public class AirportData {
	@Id
	private UUID id;
	private String iataCode;
	private Integer timezone;

	public AirportData(String iataCode, Integer timezone) {
		super();
		this.iataCode = iataCode;
		this.timezone = timezone;
	}
}
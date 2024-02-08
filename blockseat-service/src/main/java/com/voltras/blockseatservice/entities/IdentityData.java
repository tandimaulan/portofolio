package com.voltras.blockseatservice.entities;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class IdentityData {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	private String number;
	private String nationality;
	private String issuingCountry;
	private LocalDate expirationDate;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachmentid", referencedColumnName = "id")
	private PassengerAttachment attachment;
	
	public IdentityData(String number, String nationality, String issuingCountry, LocalDate expirationDate) {
		super();
		this.number = number;
		this.nationality = nationality;
		this.issuingCountry = issuingCountry;
		this.expirationDate = expirationDate;
	}
	
	
}

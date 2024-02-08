package com.voltras.ppob.service.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
public class Prefix {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private String phoneNumber;
	private String prefixNumber;
	private String provider;

	public Prefix(String phoneNumber, String prefixNumber, String provider) {
		super();
		this.phoneNumber = phoneNumber;
		this.prefixNumber = prefixNumber;
		this.provider = provider;
	}
}
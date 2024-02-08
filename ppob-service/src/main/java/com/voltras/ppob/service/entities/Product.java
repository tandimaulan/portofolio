package com.voltras.ppob.service.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	@Column(name = "productGroup")
	private String group; // cnth PLN
	private String type; // cnth PLN-PREPAID
	private String code; // cnth PLNPRE-20
	private String voucherName;
	private String codeDescription; // cnth Pembayaran PLN Prabayar Sebesar Rp. 20000
	private Double nominal; // cnth 20000
	private Double adminCommission;
	private Boolean isActive;
	

	public Product(String group, String type, String code, String voucherName, String codeDescription, Double nominal,
			Boolean isActive) {
		super();
		this.group = group;
		this.type = type;
		this.code = code;
		this.voucherName = voucherName;
		this.codeDescription = codeDescription;
		this.nominal = nominal;
		this.isActive = isActive;
	}

	public Product(String code, String codeDescription, Boolean isActive) {
		super();
		this.code = code;
		this.codeDescription = codeDescription;
		this.isActive = isActive;
	}
}

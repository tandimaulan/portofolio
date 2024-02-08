package com.voltras.ppob.service.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductDetail {
	@Id
	private UUID id;
	private String code;
	private String productCode;
	private Double nominal;
	private String voucherName;
	private String description;
	private Double adminCommission;
	
}


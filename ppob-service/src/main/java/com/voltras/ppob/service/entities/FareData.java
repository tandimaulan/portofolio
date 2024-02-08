package com.voltras.ppob.service.entities;

import java.util.UUID;

import com.voltras.ppob.api.models.ProductCode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FareData {
	@Id
	private UUID id;
	private ProductCode productCode;
	private String number;
	private String power;
	private Double nominal;
	private Double price;
	private Double adminFee;
	private Double penaltie;
	private Double totalPrice;
	private Double channelDiscount;
	private Double ntsa;
}

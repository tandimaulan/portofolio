package com.voltras.blockseatservice.entities;

import java.util.UUID;

import com.voltras.blockseat.api.enums.CabinClass;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(indexes = { @Index(name = "fare_data_idx", columnList = "inventoryId") })
public class FareData {

	@Id
	private UUID id;
	private UUID inventoryId;
	@Enumerated(EnumType.STRING)
	private CabinClass cabinClass;
	private String subClass;
	private Double price;
	private Double downPaymentPrice;
	private Integer allotment;
	private Integer downPayment;
	private Integer totalPaidOff;
	private Boolean isActive;

	public FareData(UUID inventoryId, CabinClass cabinClass, String subClass, Double price) {
		super();
		this.inventoryId = inventoryId;
		this.cabinClass = cabinClass;
		this.subClass = subClass;
		this.price = price;

	}

	// create
	public FareData(UUID inventoryId, String subClass, Double price, Integer allotment) {
		super();
		this.id = UUID.randomUUID();
		this.inventoryId = inventoryId;
		this.subClass = subClass;
		this.price = price;
		this.allotment = allotment;
		this.isActive = true;
	}

	// edit
	public FareData(UUID id, UUID inventoryId, String subClass, Double price, Integer allotment, Double downPaymentPrice, Boolean isActive) {
		super();
		this.id = id;
		this.inventoryId = inventoryId;
		this.subClass = subClass;
		this.price = price;
		this.allotment = allotment;
		this.downPaymentPrice = downPaymentPrice;
		this.isActive = isActive;
	}

}

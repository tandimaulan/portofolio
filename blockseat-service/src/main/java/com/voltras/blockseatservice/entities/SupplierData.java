package com.voltras.blockseatservice.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(indexes = { @Index(name = "supplier_data_idx", columnList = "inventoryId") })
public class SupplierData {

	@Id
	UUID id;
	UUID inventoryId;
	String supplierName;
	String supplierEmail;

	// create
	public SupplierData(UUID inventoryId, String supplierName, String supplierEmail) {
		super();
		this.id = UUID.randomUUID();
		this.inventoryId = inventoryId;
		this.supplierName = supplierName;
		this.supplierEmail = supplierEmail;
	}

	// edit
	public SupplierData(UUID id, UUID inventoryId, String supplierName, String supplierEmail) {
		super();
		this.id = id;
		this.inventoryId = inventoryId;
		this.supplierName = supplierName;
		this.supplierEmail = supplierEmail;
	}

}

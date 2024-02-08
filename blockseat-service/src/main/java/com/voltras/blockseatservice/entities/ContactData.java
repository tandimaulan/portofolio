package com.voltras.blockseatservice.entities;

import java.io.Serializable;
import java.util.UUID;

import com.voltras.blockseat.api.enums.PersonType;
import com.voltras.blockseat.api.enums.Title;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ContactData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private UUID id;

	private UUID journeyOptId;
	// agent
	private String agentFirstName;
	private String agentLastName;
	private String agentPhone;
	private String agentEmail;
	// customer
	private String customerName;
	private String customerPhoneNumber;
	private String customerEmail;

	@Enumerated(EnumType.STRING)
	private Title title;
	@Enumerated(EnumType.STRING)
	private PersonType type;

	// Customer
	public ContactData(String customerName, String customerPhoneNumber, String customerEmail) {
		super();
		this.customerName = customerName;
		this.customerPhoneNumber = customerPhoneNumber;
		this.customerEmail = customerEmail;
	}

	public ContactData(String agentFirstName, String agentLastName, String agentPhone, String agentEmail) {
		super();
		this.agentFirstName = agentFirstName;
		this.agentLastName = agentLastName;
		this.agentPhone = agentPhone;
		this.agentEmail = agentEmail;
	}

}

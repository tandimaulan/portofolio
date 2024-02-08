package com.voltras.ppob.service.entities;

import java.util.UUID;

import com.voltras.core.common.api.enums.ContactType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContactData {
	@Id
	@GeneratedValue
	private UUID id;

	public ContactData(String name, String email, String phone, ContactType type) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.type = type;
	}

	private String name;
	private String email;
	private String phone;
	private ContactType type;
}

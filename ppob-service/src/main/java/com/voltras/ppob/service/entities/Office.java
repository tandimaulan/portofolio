package com.voltras.ppob.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Office {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	private String address;
	private String code;
	private String email;
	private String fax;
	@Column(columnDefinition = "text")
	private String logo;
	private String name;
	private String packageName;
	private String phone;
	private String principal;

	public Office(String address, String code, String email, String fax, String logo, String name, String packageName,
			String phone, String principal) {
		super();
		this.address = address;
		this.code = code;
		this.email = email;
		this.fax = fax;
		this.logo = logo;
		this.name = name;
		this.packageName = packageName;
		this.phone = phone;
		this.principal = principal;
	}
}
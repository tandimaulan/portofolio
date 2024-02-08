package com.voltras.blockseatservice.entities;

import javax.validation.constraints.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class PricingComponent {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@Size(max = 4096)
	private String description;
	@Enumerated(EnumType.STRING)
	private PricingComponentType type;
	@Size(max = 4096)
	private String value;
	
	public PricingComponent(String name, String description, PricingComponentType type, String value) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.value = value;
	}
}
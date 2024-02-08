package com.voltras.ppob.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MarkupValue {
	@Id
	private Long id;
	private String packageName;
	private String productCode;
	private String customMarkup;
	private String status;
	
}

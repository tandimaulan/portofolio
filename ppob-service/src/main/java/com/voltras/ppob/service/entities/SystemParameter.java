package com.voltras.ppob.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SystemParameter {
	@Id
	private String id;
	private String key;
	@Column(columnDefinition = "text")
	private String value;
}

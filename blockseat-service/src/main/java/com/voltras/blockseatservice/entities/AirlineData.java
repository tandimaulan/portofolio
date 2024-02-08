package com.voltras.blockseatservice.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(indexes = { @Index(columnList = "operatingCode", unique = true) })
public class AirlineData {
	@Id
	@GeneratedValue
	private UUID id;
	private String operatingCode;
	private String operatingName;
	@Column(columnDefinition = "text")
	private String logoUrl;
}
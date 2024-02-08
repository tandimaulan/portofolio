package com.voltras.ppob.service.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
public class ReversalData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date requestDate;
	private String transactionId;
	private String goblinAccountId;
	private Date actionDate;
	private String description;

	public ReversalData(String transactionId, String goblinAccountId, String description) {
		super();
		this.requestDate = new Date();
		this.transactionId = transactionId;
		this.goblinAccountId = goblinAccountId;
		this.description = description;
	}
}

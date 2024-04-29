package com.voltras.helpdesk.service.entities;

import jakarta.persistence.Column;
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
public class EmailTemplate {
	@Id
	@GeneratedValue
	private Long id;
	private String templateName;
	private String subject;
	@Column(columnDefinition = "text")
	private String content;
	private String lang;
	private String mimeType;
}
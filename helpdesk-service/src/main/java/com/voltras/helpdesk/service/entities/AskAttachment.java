package com.voltras.helpdesk.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ask_attachment")
public class AskAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long size;
	@Column(columnDefinition = "text")
	private String base64String;
	@Column(name = "content_type")
	private String contentType;

	public AskAttachment(String name, Long size, String base64String, String contentType) {
		super();
		this.name = name;
		this.size = size;
		this.base64String = base64String;
		this.contentType = contentType;
	}
}
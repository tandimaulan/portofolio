package com.voltras.helpdesk.service.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Ask {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sender;
	private String subject;
	private String type;
	private Boolean isSend;
	private String bookingCode;
	private String bankName;
	private String bankCode;
	private String accountName;
	private String accountNumber;
	private String branchBank;
	private String phoneNumber;
	@Column(columnDefinition = "text")
	private String body;
	private Date createdAt;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ask_id", referencedColumnName = "id")
	private List<AskAttachment> attachments;

	public Ask(String sender, String subject, String type, String bookingCode, String bankName, String bankCode,
			String accountName, String accountNumber, String branchBank, String phoneNumber, String body,
			List<AskAttachment> attachments) {
		super();
		this.sender = sender;
		this.subject = subject;
		this.type = type;
		this.isSend = false;
		this.bookingCode = bookingCode;
		this.bankName = bankName;
		this.bankCode = bankCode;
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.branchBank = branchBank;
		this.phoneNumber = phoneNumber;
		this.body = body;
		this.attachments = attachments;
		this.createdAt = new Date();
	}
}
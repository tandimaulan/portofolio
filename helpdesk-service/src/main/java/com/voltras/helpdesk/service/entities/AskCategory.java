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
@Table(name = "ask_category")
public class AskCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(name="need_body")
	private Boolean needBody;
	@Column(name="need_booking_code")
	private Boolean needBookingCode;
	@Column(name="need_bank_name")
	private Boolean needBankName;
	@Column(name="need_bank_code")
	private Boolean needBankCode;
	@Column(name="need_account_name")
	private Boolean needAccountName;
	@Column(name="need_account_number")
	private Boolean needAccountNumber;
	@Column(name="need_branch_bank")
	private Boolean needBranchBank;
	@Column(name="need_phone_number")
	private Boolean needPhoneNumber;
}
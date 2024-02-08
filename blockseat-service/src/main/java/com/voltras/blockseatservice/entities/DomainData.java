package com.voltras.blockseatservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DomainData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Boolean isB2c;
	private String defaultEmailSender;
	private String defaultOfficeCode;
	private String defaultOfficeName;
	private String defaultOfficePackage;
	private String defaultPrincipal;
	private String name;
	private String code;
	private String url;
	private String defaultLogo;
	private String backgroundColor;
	private String productName;
	private String defaultOfficeAddress;
	private String defaultOfficeEmail;
	private String defaultOfficeFax;
	private String defaultOfficePhone;
	private String receiptBackground;
	private String voucherBackground;

	private Boolean showTicketPrice = false;
	private Boolean showPaxPrice = false;
	private Boolean showDiscount = false;
	private Boolean showTotalPrice = false;
	private Boolean showChannelDiscount = false;
	private Boolean showSaServiceFee = false;
	private Boolean showHandlingFee = false;
	private Boolean showTax = false;
	private Boolean showAdminFee = false;
}

package com.voltras.ppob.service.entities;

import java.util.Date;
import java.util.UUID;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.ppob.api.models.ProductCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookData {
	@Id
	private UUID bookId;
	private String bookingCode;
	private Date bookDate;
	private String bookedBy;
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	private String customerProductNumber;
	private Double totalPrice;
	private Date timelimit;
	private String domain;
	private String goblinAccountId;
	private String providerTransactionId;
	private String trxId;
	private String reversalTransactionId;
	private String officeCode;
	private String officeName;
	private String officePackage;
	private Date cancelAt;
	private String paymentChannel;
	private Boolean isChecking = false;
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	@Enumerated(EnumType.STRING)
	private ProductCode productType;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	private String productCode;
	private String refNum;
	private String paidBy;
	private String canceledBy;
	private Date paymentDate;
	private Double totalPayment;
	private Date createdAt;
	private String orderId;
	private String detail;// for detail BPJS-POSTPAID & PDAM
	private String customerName;
	private String token;// for PLN-PREPAID
	private String fareAndPower;// for PLN-PREPAID n PLN-POSTPAID
	private String totalKwh;// for PLN-PREPAID n PLN-POSTPAID
	private String serialNumber;// for Voucher
	private String virtualAccountNumber;// for BPJS
	private String totalMonth;
	private String period;
	private Double nta;
	private Double ntsa;
	private String gatewayStatus;// Status from gateway
	private String responseCodeMessage;// message from gateway
	private String additionalInfo;// additional info from gateway
	private String msn; //machine number pln-prepaid
	
	//for reversal data
	private String transactionId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "contactid", referencedColumnName = "id")
	private ContactData contactData;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private ProductDetail productDetail;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "financial_id", referencedColumnName = "id")
	private FinancialStatement financialStatement;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "fare_id", referencedColumnName = "id")
	private FareData fareData;
	

	// new book
	public BookData(UUID bookId, String bookingCode, Date bookDate, BookingStatus status, String customerProductNumber,
			Date timelimit, String domain, String officeCode, String officeName, String officePackage,
			ProductCode productType, String trxId, Boolean isChecking, ProductDetail productDetail) {
		super();
		this.bookId = bookId;
		this.bookingCode = bookingCode;
		this.bookDate = bookDate;
		this.status = status;
		this.customerProductNumber = customerProductNumber;
		this.timelimit = timelimit;
		this.domain = domain;
		this.officeCode = officeCode;
		this.officeName = officeName;
		this.officePackage = officePackage;
		this.productType = productType;
		this.trxId = trxId;
		this.isChecking = isChecking;
		this.productDetail = productDetail;
	}

}

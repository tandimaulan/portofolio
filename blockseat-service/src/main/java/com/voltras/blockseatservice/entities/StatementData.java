package com.voltras.blockseatservice.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = { @Index(name = "statement_data_idx", columnList = "id") })
public class StatementData {
	@Id
	@GeneratedValue
	private UUID id;
	private String inventoryId;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bookid", referencedColumnName = "id")
	private BookData bookData;

	private Integer seatBooked;
	private Double pricePerSeat;
	private Double totalPrice;
	private Double totalPayment;
	private Double downPayment;
	private Double totalDownPayment;
	private LocalDateTime paymentDate;
	private String providerTransactionId;
	private String transactionId;
	private String paymentChannel;
	private String paidBy;
	private String orderId;
	private LocalDateTime timelimitAt;
	private Boolean isChecking = false;
	private LocalDateTime createdAt;

	public StatementData(PaymentStatus paymentStatus, Integer seatBooked, Double pricePerSeat, Double totalPrice,
			Double totalPayment, Double downPayment, LocalDateTime paymentDate, String providerTransactionId,
			String transactionId, String paidBy, String inventoryId) {
		super();
		this.paymentStatus = paymentStatus;
		this.seatBooked = seatBooked;
		this.pricePerSeat = pricePerSeat;
		this.totalPrice = totalPrice;
		this.totalPayment = totalPayment;
		this.downPayment = downPayment;
		this.paymentDate = paymentDate;
		this.providerTransactionId = providerTransactionId;
		this.transactionId = transactionId;
		this.paidBy = paidBy;
		this.inventoryId = inventoryId;
		this.isChecking = false;
	}

	public StatementData(PaymentStatus paymentStatus, PaymentType paymentType) {
		super();
		this.paymentStatus = paymentStatus;
		this.paymentType = paymentType;
	}

}

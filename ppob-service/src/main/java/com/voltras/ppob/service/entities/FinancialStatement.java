package com.voltras.ppob.service.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class FinancialStatement {
	@Id
	@GeneratedValue
	private UUID id;
	private Double ticketPrice;
	private Double paxPrice;
	private Double totalPrice;
	private Double nta;
	private Double supplierCommision;
	private Double voltrasMarkup;
	private Double discount;
	private Double saServiceFee;
	private Double handlingFee;
	private Double saHandlingFeePortion;
	private Double tax;
	private Double ppnPercent;
	private Double aPercent;
	private Double flat;
	private Double bPercent;
	private Double channelDiscount;
	private Double xPercent;
	private Double vat;
	private Double adminFee;
	private Double insurance;
	private String commissionExpression;
	private Boolean commissionWithTax;

	public FinancialStatement(Double ticketPrice, Double paxPrice, Double totalPrice, Double nta,
			Double supplierCommision, Double voltrasMarkup, Double discount, Double saServiceFee, Double handlingFee,
			Double saHandlingFeePortion, Double tax, Double ppnPercent, Double aPercent, Double flat, Double bPercent,
			Double channelDiscount, Double xPercent, Double vat, Double adminFee, Double insurance,
			String commissionExpression, Boolean commissionWithTax) {
		super();
		this.ticketPrice = ticketPrice;
		this.paxPrice = paxPrice;
		this.totalPrice = totalPrice;
		this.nta = nta;
		this.supplierCommision = supplierCommision;
		this.voltrasMarkup = voltrasMarkup;
		this.discount = discount;
		this.saServiceFee = saServiceFee;
		this.handlingFee = handlingFee;
		this.saHandlingFeePortion = saHandlingFeePortion;
		this.tax = tax;
		this.ppnPercent = ppnPercent;
		this.aPercent = aPercent;
		this.flat = flat;
		this.bPercent = bPercent;
		this.channelDiscount = channelDiscount;
		this.xPercent = xPercent;
		this.vat = vat;
		this.adminFee = adminFee;
		this.insurance = insurance;
		this.commissionExpression = commissionExpression;
		this.commissionWithTax = commissionWithTax;
	}
}

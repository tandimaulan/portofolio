package com.voltras.ppob.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.ppob.service.components.PriceEvaluator;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.entities.FinancialStatement;

@Service
public class FinancialStatementService {

	@Autowired
	private PriceEvaluator evaluator;
	@Autowired
	private SystemParameterService systemParameter;

	public FinancialStatement fromTotal(BookData data, Double nta, Double total, String commissionExpression,
			Double voltrasMarkup) {

		Double supplierCommision = total - (nta + voltrasMarkup);

		Double discount = 0D;
		Double flat = 0D;
		if (commissionExpression == null) {
			commissionExpression = "0";
		}
		Double mf = evaluator.evaluateCommission(commissionExpression.replaceAll("%", "/100"), total, nta);
		Double handlingFee = mf < 0 ? -mf : 0D;
		Double aPercent = mf < 0 ? (handlingFee - flat) * 100 / (total - discount) : 0D;
		Double xPercent = mf > 0 ? mf * 100 / supplierCommision : 0D;

		Boolean commissionWithTax = Boolean.valueOf(systemParameter.get("commissionWithTax"));
		Double ppnPercent = Double.valueOf(systemParameter.get("ppnPercent"));
		Double vat = Double.valueOf(systemParameter.get("vat"));

		Double tax = mf < 0 ? supplierCommision * ppnPercent / 100
				: mf * (ppnPercent + vat) / (100 - (commissionWithTax ? ppnPercent : 0));

		Double taxedChannelDiscount = 0D;
		Double channelDiscount = 0D;
		if (mf < 0D) {
		} else if (commissionWithTax) {
			taxedChannelDiscount = mf;
			channelDiscount = mf + tax;
		} else {
			taxedChannelDiscount = mf - tax;
			channelDiscount = mf;
		}

		Double saServiceFee = 0D;
		Double adminFee = 0D;
		Double paxPrice = (total - discount) + saServiceFee;

		Double bPercent = 0D / 100;
		Double saHandlingFeePortion = (total - discount) * bPercent + flat;

		Double totalPrice = (total - discount) + handlingFee
				- (commissionWithTax ? taxedChannelDiscount : channelDiscount);

//		Double totalPrice = Math.ceil(price);

		return new FinancialStatement(paxPrice, paxPrice, totalPrice, nta, supplierCommision, voltrasMarkup, discount,
				saServiceFee, handlingFee, saHandlingFeePortion, taxedChannelDiscount, ppnPercent, aPercent, flat,
				bPercent, channelDiscount, xPercent, vat, adminFee, 0D, commissionExpression, commissionWithTax);

	}

	public FinancialStatement recreate(FinancialStatement oldStatement) {
		return new FinancialStatement(oldStatement.getTicketPrice(), oldStatement.getPaxPrice(),
				oldStatement.getTotalPrice(), oldStatement.getNta(), oldStatement.getSupplierCommision(),
				oldStatement.getVoltrasMarkup(), oldStatement.getDiscount(), oldStatement.getSaServiceFee(),
				oldStatement.getHandlingFee(), oldStatement.getSaHandlingFeePortion(), oldStatement.getTax(),
				oldStatement.getPpnPercent(), oldStatement.getAPercent(), oldStatement.getFlat(),
				oldStatement.getBPercent(), oldStatement.getChannelDiscount(), oldStatement.getXPercent(),
				oldStatement.getVat(), oldStatement.getAdminFee(), oldStatement.getInsurance(),
				oldStatement.getCommissionExpression(), oldStatement.getCommissionWithTax());
	}
}

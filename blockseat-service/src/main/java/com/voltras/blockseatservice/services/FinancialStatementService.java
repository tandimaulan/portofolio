package com.voltras.blockseatservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.api.exceptions.SeatNotAvailableException;
import com.voltras.blockseatservice.components.PriceEvaluator;
import com.voltras.blockseatservice.entities.BookData;
import com.voltras.blockseatservice.entities.FinancialStatement;

@Service
public class FinancialStatementService {
	@Autowired
	private PriceEvaluator evaluator;
	@Autowired
	private SystemParameterService systemParameter;

	public FinancialStatement fromInventoryAndFare(BookData data, Double nta, Integer paxCount,
			String commissionExpression) throws SeatNotAvailableException {

		if (commissionExpression == null) {
			commissionExpression = "0";
		}
		Double voltrasMarkup = Double.valueOf(systemParameter.get("voltrasMarkup"));
		Double supplierCommision = Double.valueOf(systemParameter.get("supplierCommision"));
		Double ticketPrice = supplierCommision + nta + voltrasMarkup;
		Double downPayment = data.getDownPaymentPrice();
		Double discount = 0D;
		Double flat = 0D;

		Double mf = evaluator.evaluateCommission(commissionExpression, nta, ticketPrice);
//		System.out.println(mf);
		Double handlingFee = mf < 0 ? mf : 0D;
		Double aPercent = mf < 0 ? (handlingFee - flat) / (ticketPrice - discount) : 0D;

		Double ppnPercent = Double.valueOf(systemParameter.get("ppnPercent")) / 100;
		Double vat = Double.valueOf(systemParameter.get("vat")) / 100;
		Double tax = handlingFee * (ppnPercent + vat) / 100;

		Double saServiceFee = 0D;
		Double adminFee = 0D;
		Double paxPrice = (ticketPrice * paxCount - discount) + saServiceFee + handlingFee + tax + adminFee;
		Double paxDownPayment = (downPayment * paxCount - discount) + saServiceFee + handlingFee + tax + adminFee;

		Double channelDiscount = mf > 0 ? mf : 0D;
		Double xPercent = mf > 0 ? mf / supplierCommision : 0D;

		Double bPercent = Double.valueOf(systemParameter.get("bPercent")) / 100;
		Double saHandlingFeePortion = (ticketPrice - discount) * bPercent + flat;

		Double totalDownPaymentPrice = paxDownPayment - channelDiscount - saHandlingFeePortion;
		Double totalPrice = paxPrice - channelDiscount - saHandlingFeePortion;

		Double insurance = 0D;
		return new FinancialStatement(ticketPrice, paxPrice, totalPrice, totalDownPaymentPrice, supplierCommision,
				voltrasMarkup, discount, saServiceFee, handlingFee, saHandlingFeePortion, tax, ppnPercent, aPercent,
				flat, bPercent, channelDiscount, xPercent, vat, adminFee, insurance, insurance);
	}

	public FinancialStatement getBeforeIssue(FinancialStatement financialStatement, Double adminFee) {
		financialStatement.setAdminFee(adminFee);
		financialStatement.setPaxPrice(financialStatement.getPaxPrice() + adminFee);
		financialStatement.setTotalPrice(financialStatement.getTotalPrice() + adminFee);
		return financialStatement;
	}

	public FinancialStatement recreate(FinancialStatement financialStatement) {
		return new FinancialStatement(financialStatement.getTicketPrice(), financialStatement.getPaxPrice(),
				financialStatement.getTotalPrice(), financialStatement.getDownPaymentPrice(),
				financialStatement.getNta(), financialStatement.getSupplierCommision(),
				financialStatement.getVoltrasMarkup(), financialStatement.getDiscount(),
				financialStatement.getSaServiceFee(), financialStatement.getHandlingFee(),
				financialStatement.getSaHandlingFeePortion(), financialStatement.getTax(),
				financialStatement.getPpnPercent(), financialStatement.getAPercent(), financialStatement.getFlat(),
				financialStatement.getBPercent(), financialStatement.getChannelDiscount(),
				financialStatement.getXPercent(), financialStatement.getVat(), financialStatement.getAdminFee(),
				financialStatement.getInsurance());
	}
}

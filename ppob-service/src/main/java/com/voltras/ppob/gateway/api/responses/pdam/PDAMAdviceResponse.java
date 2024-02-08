package com.voltras.ppob.gateway.api.responses.pdam;

import java.time.LocalDateTime;

public record PDAMAdviceResponse(String status, String responseCode, String responseCodeMessage, String text,
		LocalDateTime date, String refNum, String subId, String name, String subAddress, String subSegment,
		Double adminCharges, Double transactionAmount, Integer meterStart, Integer meterEnd, String billMonth,
		Double billPdam, Double billTotal, Double installment, Double stamp, Double vat, Double danaMeter,
		Double penalty, String info, String transactionId) {
}

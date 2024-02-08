package com.voltras.ppob.gateway.api.responses.pdam;

public record PDAMPaymentResponse(
		// MANDATORY
		String status, String rc, String rcm, String text, String dt, String refnum, String subid, String name,
		String subaddress, String subsegmen, Double admincharges, Double transamount, Integer metrstart,
		Integer metrend, String billmonth, Double billpdam, Double billtotal, Double installment, Double stamp,
		Double vat, Double danameter, Double penalty, String info, String trxid, Double adminpdam, Integer mtrstr,
		Integer mtrend, Integer waste) {
}

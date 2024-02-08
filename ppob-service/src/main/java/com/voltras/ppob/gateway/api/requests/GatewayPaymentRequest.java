package com.voltras.ppob.gateway.api.requests;

public record GatewayPaymentRequest(String cid, String dt, String hc, String modul, String command, String msn,
		String idpel, String detail, String biller, String input1, String tujuan, String amount, String nominal,
		String resp, String trxid, String refnum, String voucherId) {
}
/*
 * String msn number for PLN-PREPAID String idpel number for PLN-POSTPAID String
 * detail condition for PLN-POSTPAID true or false String biller for BPJS String
 * input1 number for BPJS String amount amount for BPJS
 * 
 */

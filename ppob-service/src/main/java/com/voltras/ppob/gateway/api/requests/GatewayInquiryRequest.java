package com.voltras.ppob.gateway.api.requests;

public record GatewayInquiryRequest(String cid, String dt, String hc, String modul, String command, String trxid, String resp,
		String idpel, String tujuan, String biller, String detail, String nominal, String voucherId, String msn,
		String input1) {
}

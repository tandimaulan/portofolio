package com.voltras.ppob.gateway.api.requests.bpjs;

public record BPJSPaymentRequest(String cid, String dt, String hc, String modul,
		String command, String trxid, String resp, String biller, String input1, String amount) {

}

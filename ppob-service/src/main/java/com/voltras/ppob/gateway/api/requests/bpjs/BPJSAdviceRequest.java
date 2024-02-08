package com.voltras.ppob.gateway.api.requests.bpjs;

public record BPJSAdviceRequest(String clientId, String transactionDate, String credential, String modul,
		String command, String transactionId, String responseType, String biller, String paymentNumber, String amount) {

}

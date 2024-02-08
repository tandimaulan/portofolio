package com.voltras.ppob.gateway.api.requests;

public record AdviceRequest(String clientId, String transactionDate, String credential, String modul, String command,
		String transactionId, String responseType, String customerId, String destination, String biller, String detail,
		String nominal, String refnum) {
}

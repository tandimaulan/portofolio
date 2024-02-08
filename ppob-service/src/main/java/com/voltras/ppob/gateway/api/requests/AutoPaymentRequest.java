package com.voltras.ppob.gateway.api.requests;

public record AutoPaymentRequest(String clientId, String transactionDate, String credential, String modul, String command,
		String transactionId, String responseType, String customerId, String nominal) {
}

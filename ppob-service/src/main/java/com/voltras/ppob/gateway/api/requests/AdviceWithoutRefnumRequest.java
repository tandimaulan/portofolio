package com.voltras.ppob.gateway.api.requests;

public record AdviceWithoutRefnumRequest(String clientId, String transactionDate, String credential, String modul, String command,
		String transactionId, String responseType, String customerId, String nominal) {
}

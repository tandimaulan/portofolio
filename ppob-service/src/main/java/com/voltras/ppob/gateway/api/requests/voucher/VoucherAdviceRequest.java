package com.voltras.ppob.gateway.api.requests.voucher;

public record VoucherAdviceRequest(String clientId, String transactionDate, String credential, String modul,
		String command, String destination, String voucherId, String transactionId, String responseType) {

}

package com.voltras.ppob.gateway.api.requests.voucher;

import java.util.List;

public record VoucherInfoRequest(String clientId, String transactionDate, String credential, String responseType,
		String command, List<String> voucherId, String modul, String transactionId) {
}

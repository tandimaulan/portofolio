package com.voltras.ppob.gateway.api.responses.voucher;

import java.time.LocalDateTime;

public record VoucherAdviceResponse(String status, String responseCode, String responseCodeMessage, String text,
		LocalDateTime date, String refNum, String voucherId, String number, Double nominal, Double price,
		String serialNumber, String transactionId) {
}

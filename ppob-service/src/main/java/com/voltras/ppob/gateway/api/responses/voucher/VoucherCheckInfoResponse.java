package com.voltras.ppob.gateway.api.responses.voucher;

public record VoucherCheckInfoResponse(String status, String responseCode, String responseCodeMessage, DetailInfoVoucher detailInfoVoucher, String transactionId) {
	
}

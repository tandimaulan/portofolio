package com.voltras.ppob.gateway.api.responses.voucher;

public record VoucherInquiryResponse(String status, String rc, String rcm, String text, String refnum, String voucherid,
		String nomor, Double nominal, Double harga, String trxid) {
}
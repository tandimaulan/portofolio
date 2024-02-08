package com.voltras.ppob.gateway.api.responses.voucher;

public record VoucherPaymentResponse(String status, String rc, String rcm, String text,
		String tanggal, String refnum, String voucherid, String nomor, Double nominal, Double harga,
		String serialnumber, String trxid) {
}

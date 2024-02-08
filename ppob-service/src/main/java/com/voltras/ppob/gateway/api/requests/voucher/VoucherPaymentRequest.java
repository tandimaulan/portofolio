package com.voltras.ppob.gateway.api.requests.voucher;

public record VoucherPaymentRequest(String cid, String dt, String hc, String modul, String command, String trxid,
		String tujuan, String resp, String voucherId) {

}

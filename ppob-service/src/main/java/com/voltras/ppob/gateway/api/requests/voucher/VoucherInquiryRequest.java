package com.voltras.ppob.gateway.api.requests.voucher;

public record VoucherInquiryRequest(String cid, String dt, String hc, String modul,
		String Command, String trxid, String tujuan, String resp, String voucherId) {

}

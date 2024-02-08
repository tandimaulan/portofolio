package com.voltras.ppob.gateway.api.responses.bpjs;

public record BPJSInquiryResponse(String status, String rc, String rcm, String text,
		String refnum, String input1, Double jmltagihan, Double admin, Double totaltag, DetailInquiryBPJS data,
		String trxid) {
}

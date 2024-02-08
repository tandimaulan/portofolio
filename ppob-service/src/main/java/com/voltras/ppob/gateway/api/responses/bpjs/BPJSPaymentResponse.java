package com.voltras.ppob.gateway.api.responses.bpjs;

public record BPJSPaymentResponse(String status, String rc, String rcm, String text,
		String refnum, String input1, Double jmltagihan, Double admin, Double totaltag,
		DetailPaymentBPJS data, String infotext, String trxid) {
}

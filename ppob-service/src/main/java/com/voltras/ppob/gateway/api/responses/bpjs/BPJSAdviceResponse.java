package com.voltras.ppob.gateway.api.responses.bpjs;

public record BPJSAdviceResponse(String status, String responseCode, String responseCodeMessage, String text,
		String refNum, String payNumber, Double billAmount, Double admin, Double totalTag,
		DetailAdviceBPJS detailAdviceBPJS, String infoText, String transactionId) {
}

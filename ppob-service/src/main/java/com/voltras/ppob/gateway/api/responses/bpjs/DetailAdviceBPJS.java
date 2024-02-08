package com.voltras.ppob.gateway.api.responses.bpjs;

public record DetailAdviceBPJS(String virtualAccount, String customerName, String totalMonth, String telephone,
		String totalParticipants, String balance, String premi, String transactionId) {
}

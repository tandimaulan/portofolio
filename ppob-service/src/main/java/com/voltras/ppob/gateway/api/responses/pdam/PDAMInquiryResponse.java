package com.voltras.ppob.gateway.api.responses.pdam;

public record PDAMInquiryResponse(String status, String rc, String rcm, String text, String refnum, String subid,
		String name, String totalperiod, String billperiod, String admincharge, String transamount, String trxid

) {
}

package com.voltras.ppob.gateway.api.requests.pdam;

public record PDAMInquiryRequest(String cid, String dt, String hc, String modul,
		String command, String trxid, String idpel, String resp, String detail, String biller) {

}

package com.voltras.ppob.gateway.api.requests.plnpostpaid;

public record PLNPostpaidInquiryRequest(String cid, String dt, String hc, String modul,
		String command, String trxid, String idpel, String resp, String Detail) {

}

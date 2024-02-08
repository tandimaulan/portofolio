package com.voltras.ppob.gateway.api.requests.plnpostpaid;

public record PLNPostpaidAdviceRequest(String cid, String dt, String hc, String modul,
		String command, String idpel, String refnum, String Detail, String resp, String trxid) {

}

package com.voltras.ppob.gateway.api.requests.plnprepaid;

public record PLNPrepaidAutoPaymentRequest(String cid, String dt, String hc, String modul,
		String command, String msn, String nominal, String resp, String trxid) {

}

package com.voltras.ppob.gateway.api.requests.plnprepaid;

public record PLNPrepaidAdviceRequest(String cid, String dt, String hc, String modul,
		String command, String msn, String nominal, String refNum, String resp, String trxId
		) {

}

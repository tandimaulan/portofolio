package com.voltras.ppob.gateway.api.responses.plnprepaid;

public record PLNPrepaidAutoPymentResponse(String status, String rc, String rcm, String text, String refnum, String msn,
		String subid, String nama, String tarifdaya, String nominal, String jmlkwh, String token, String ppn,
		Integer ppj, String admin, String angsuran, String materai, String info, Double rptoken, String distcode,
		String sunit, String suphone, String maxkwh, String trxid) {
}

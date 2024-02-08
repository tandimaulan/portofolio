package com.voltras.ppob.gateway.api.responses.plnpostpaid;

public record PLNPostpaidInquiryResponse(String status, String rc, String rcm, String text, String refnum, String subid,
		String nama, String bulan, String blth, String tagihan, String admin, String total, String tarif,
		String trxid) {
}

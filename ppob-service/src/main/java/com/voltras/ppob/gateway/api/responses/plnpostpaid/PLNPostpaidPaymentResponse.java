package com.voltras.ppob.gateway.api.responses.plnpostpaid;

public record PLNPostpaidPaymentResponse(
		// Mandatory
		String status, String rc, String rcm, String text, String refnum, String subid, String nama, String standmeter,
		String blth, String ppn, String denda, String tagihan, String admin, String total, String tarif, String info1,
		String info2, String trxid,
		// Optional
		// jika Detail pada request = true data optional ditampilkan
		// jika false tidak ditampilkan
		String bln_1, String meter_1, String tagbln_1, String admin_1, String penalty_1, String duedate_1,
		String incentive_1, String meterread1, String bln_2, String meter_2, String tagbln_2, String admin_2,
		String penalty_2, String duedate_2, String incentive_2, String meterread2, String bln_3, String meter_3,
		String tagbln_3, String admin_3, String penalty_3, String duedate_3, String incentive_3, String meterread3,
		String bln_4, String meter_4, String tagbln_4, String admin_4, String penalty_4, String duedate_4,
		String incentive_4, String meterread4, String sunit, String sphone

) {
}

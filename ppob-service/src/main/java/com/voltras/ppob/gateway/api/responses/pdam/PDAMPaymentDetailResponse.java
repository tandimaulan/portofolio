package com.voltras.ppob.gateway.api.responses.pdam;

public record PDAMPaymentDetailResponse(String status, String rc, String rcm, String text, String dt, String refnum,
		String subid, String name, String subaddress, String subsegmen, Double admincharges, Double transamount,
		Integer metrstart, Integer metrend, String billmonth, Double billpdam, Double billtotal, Double installment,
		Double stamp, Double vat, Double danameter, Double penalty, String info, String trxid, Double adminpdam,
		Integer mtrstr, Integer mtrend, Integer waste, String bln_1, Double tagihan_1, Double stamp_1, Double penalty_1,
		Double danameter_1, Double vat_1, Double waste_1, Double admpdam_1, Double adm_1, Double installment_1,
		Integer mtrstart_1, Integer mtrend_1, Integer mtruse_1, String bln_2, Double tagihan_2, Double stamp_2,
		Double penalty_2, Double danameter_2, Double vat_2, Double waste_2, Double admpdam_2, Double adm_2,
		Double installment_2, Integer mtrstart_2, Integer mtrend_2, Integer mtruse_2, String bln_3, Double tagihan_3,
		Double stamp_3, Double penalty_3, Double danameter_3, Double vat_3, Double waste_3, Double admpdam_3,
		Double adm_3, Double installment_3, Integer mtrstart_3, Integer mtrend_3, Integer mtruse_3, String bln_4,
		Double tagihan_4, Double stamp_4, Double penalty_4, Double danameter_4, Double vat_4, Double waste_4,
		Double admpdam_4, Double adm_4, Double installment_4, Integer mtrstart_4, Integer mtrend_4, Integer mtruse_4) {
}
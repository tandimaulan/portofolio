package com.voltras.ppob.gateway.api.responses.pdam;

import java.time.LocalDateTime;

public record PDAMAdviceDetailResponse(String status, String responseCode, String responseCodeMessage, String text,
		LocalDateTime date, String refNum, String subId, String name, String subAddress, String subSegment,
		Double adminCharges, Double transactionAmount, Integer meterStart, Integer meterEnd, String billMonth,
		Double billPdam, Double billTotal, Double installment, Double stamp, Double vat, Double danaMeter,
		Double penalty, String info,
		String month1, Double bill1, Double stamp1, Double penalty1, Double danaMeter1, Double vat1, Double waste1,
		Double adminPdam1, Double admin1, Double installment1, Integer meterStart1, Integer meterEnd1,
		Integer meterUse1,
		String month2, Double bill2, Double stamp2, Double penalty2, Double danaMeter2, Double vat2, Double waste2,
		Double adminPdam2, Double admin2, Double installment2, Integer meterStart2, Integer meterEnd2,
		Integer meterUse2,
		String month3, Double bill3, Double stamp3, Double penalty3, Double danaMeter3, Double vat3, Double waste3,
		Double adminPdam3, Double admin3, Double installment3, Integer meterStart3, Integer meterEnd3,
		Integer meterUse3, String month4, Double bill4, Double stamp4, Double penalty4, Double danaMeter4, Double vat4,
		Double waste4, Double adminPdam4, Double admin4, Double installment4, Integer meterStart4, Integer meterEnd4,
		Integer meterUse4, String transactionId) {
}
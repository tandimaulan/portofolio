package com.voltras.ppob.gateway.api.responses.plnpostpaid;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PLNPostpaidPaymentDetailResponse(String status, String responseCode, String responseCodeMessage,
		String text, LocalDateTime date, String refNum, String subId, String name, String fare, String standMeter,
		String month, Integer ppn, Double penalty, Double bill, Double admin, Double total, String info1, String info2,
		String month1, String meter1, Double monthBill1, Double admin1, Double penalty1, LocalDate dueDate1,
		Double incentive1, Integer meterRead1, String month2, String meter2, Double monthBill2, Double admin2,
		Double penalty2, LocalDate dueDate2, Double incentive2, Integer meterRead2, String month3, String meter3,
		Double monthBill3, Double admin3, Double penalty3, LocalDate dueDate3, Double incentive3, Integer meterRead3,
		String month4, String meter4, Double monthBill4, Double admin4, Double penalty4, LocalDate dueDate4,
		Double incentive4, Integer meterRead4, String transactionId) {
}

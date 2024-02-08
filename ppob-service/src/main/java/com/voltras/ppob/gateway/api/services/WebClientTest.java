package com.voltras.ppob.gateway.api.services;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.ppob.gateway.api.responses.plnprepaid.PLNPrepaidInquiryResponse;
import com.voltras.ppob.gateway.api.responses.voucher.VoucherInquiryResponse;
import com.voltras.ppob.utils.JsonUtils;

import reactor.core.publisher.Mono;

public class WebClientTest {
	public static <T> T GetData(Class<T> targetClass) {

		WebClient.Builder webClientBuilder = WebClient.builder();

		WebClient webClient = webClientBuilder.baseUrl("http://182.23.93.66:10063/index.php").build();

		Mono<String> responseMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/").queryParam("cid", "dev09204-899a-4164-ba6c-1845ed47v0Ltr4s")
						.queryParam("dt", "20230810")
						.queryParam("hc", "6f63c6c01b55ce6b4e8a5fb0250b9bc6d44ed967783b1ca733ca2759e9a1d010")
						.queryParam("modul", "ISI").queryParam("Command", "INQ").queryParam("trxId", "OB12345")
						.queryParam("tujuan", "081308130813").queryParam("resp", "JSON").queryParam("VoucherId", "tn5")
						.build())
				.retrieve().bodyToMono(String.class);
		responseMono.subscribe(response -> {
			try {
				var voucherInquiryResponse = (VoucherInquiryResponse) JsonUtils.readAsObject(response, targetClass);

				System.out.println("");
				System.out.println("--- Voucher Inquiry Response (Json String) ---");
				System.out.println(JsonUtils.parseToString(response));

				System.out.println("");
				System.out.println("--- Voucher Inquiry Response (Object) ---");
				System.out.println("status: " + voucherInquiryResponse.status());
				System.out.println("responseCode: " + voucherInquiryResponse.rc());
				System.out.println("responseCodeMessage: " + voucherInquiryResponse.rcm());
				System.out.println("text: " + voucherInquiryResponse.text());
				System.out.println("refNum: " + voucherInquiryResponse.refnum());
				System.out.println("voucherId: " + voucherInquiryResponse.voucherid());
				System.out.println("number: " + voucherInquiryResponse.nomor());
				System.out.println("nominal: " + voucherInquiryResponse.nominal());
				System.out.println("price: " + voucherInquiryResponse.harga());
				System.out.println("transactionId: " + voucherInquiryResponse.trxid());

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}, error -> {
			System.err.println("Error: " + error.getMessage());
		}, () -> {
			System.out.println("Completed.");
		});
		return null;
	}

	public static <T> T GetDataPLNPRE(Class<T> targetClass) {

		WebClient.Builder webClientBuilder = WebClient.builder();

		WebClient webClient = webClientBuilder.baseUrl("http://182.23.93.66:10063/index.php").build();

		Mono<String> responseMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/").queryParam("cid", "dev09204-899a-4164-ba6c-1845ed47v0Ltr4s")
						.queryParam("dt", "20230810")
						.queryParam("hc", "6f63c6c01b55ce6b4e8a5fb0250b9bc6d44ed967783b1ca733ca2759e9a1d010")
						.queryParam("modul", "PRE").queryParam("Command", "INQ").queryParam("msn", "77777777777")
						.queryParam("nominal", "2000").queryParam("resp", "JSON").queryParam("trxId", "OB12345")
						.build())
				.retrieve().bodyToMono(String.class);
		responseMono.subscribe(response -> {
			try {
				var resp = (PLNPrepaidInquiryResponse) JsonUtils.readAsObject(response, targetClass);

				var res = new PLNPrepaidInquiryResponse(resp.status(), resp.rc(), resp.rcm(), resp.text(), resp.refnum(),
						response, response, response, response, response, null, response, response, response, response,
						response, response);

				System.out.println("");
				System.out.println("--- PLN-PREPAID Inquiry Response (Json String) ---");
				System.out.println(JsonUtils.parseToString(response));

				System.out.println("");
				System.out.println("--- PLN-PREPAID Inquiry Response (Object) ---");
				System.out.println("status: " + resp.status());
				System.out.println("responseCode: " + resp.rc());
				System.out.println("responseCodeMessage: " + resp.rcm());
				System.out.println("text: " + resp.text());
				System.out.println("refNum: " + resp.refnum());
				System.out.println("msn: " + resp.msn());
				System.out.println("subId: " + resp.subid());
				System.out.println("nama: " + resp.nama());
				System.out.println("tarif: " + resp.tarif());
				System.out.println("admin: " + resp.admin());
				System.out.println("unsold: " + resp.unsold());
				System.out.println("denomunsold: " + resp.denomunsold());
				System.out.println("distcode: " + resp.distcode());
				System.out.println("sunit: " + resp.sunit());
				System.out.println("suphone: " + resp.suphone());
				System.out.println("maxkwh: " + resp.maxkwh());
				System.out.println("trxid: " + resp.trxid());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}, error -> {
			System.err.println("Error: " + error.getMessage());
		}, () -> {
			System.out.println("Completed.");
		});
		return null;
	}
}

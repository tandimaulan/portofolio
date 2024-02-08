//package com.voltras.ppob.gateway.api.components;
//
//import java.time.Duration;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//import com.voltras.ppob.utils.GenerateUtil;
//
//@Component
//public class WebHelper {
//	private static WebClient build(String url) {
//		return WebClient.builder()
//				.exchangeStrategies(ExchangeStrategies.builder()
//						.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
//				.baseUrl(url).defaultHeaders(headers -> {
//					headers.set(HttpHeaders.ACCEPT_CHARSET, "utf-8");
//					headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//				}).build();
//	}
//
//	public <T> T post(String url, MultiValueMap<String, String> form, Integer timeout, Class<T> expectedType) {
//		WebClient webClient = build(url);
//		String jsonResponse = webClient.post().body(BodyInserters.fromFormData(form)).retrieve()
//				.bodyToMono(String.class).timeout(Duration.ofSeconds(timeout)).block();
//		System.out.println(jsonResponse);
//		if (jsonResponse == null || jsonResponse.isBlank()) {
//			return null;
//		}
//		return GenerateUtil.deserializeFromVan(jsonResponse, expectedType);
//	}
//
//}




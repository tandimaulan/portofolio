package com.voltras.blockseatservice.components;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.voltras.core.common.api.exceptions.GatewayTimeoutException;

import reactor.core.publisher.Mono;

@Component
public class WebHelper {
	@Autowired
	private LogHelper logger;

	public String post(String url, String requestBody, Integer timeout, String message,
			Class<? extends Exception> clazz) throws GatewayTimeoutException {
		WebClient webClient = WebClient.builder().baseUrl(url).defaultHeaders(headers -> {
			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
		}).build();

		try {
			var jsonResponse = webClient.post().body(Mono.just(requestBody), String.class).exchangeToMono(response -> {
				if (response.statusCode().is5xxServerError()) {
					response.body((httpResponse, context) -> {
						return httpResponse.getBody();
					});
					return response.bodyToMono(String.class);
				} else {
					return response.bodyToMono(String.class);
				}
			}).timeout(Duration.ofSeconds(timeout)).block();
			return jsonResponse;
		} catch (Exception e) {
			logger.error("FAILED TO SEND REQUEST: {}", e);
			if (e.getCause() instanceof TimeoutException) {
				throw new GatewayTimeoutException(message);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public String post(String url, String requestBody, Integer timeout) throws GatewayTimeoutException {
		return post(url, requestBody, timeout, null, GatewayTimeoutException.class);
	}

	public String getSolr(String url, String requestParam, Integer timeout) throws GatewayTimeoutException {
		return getSolr(url, requestParam, timeout, null, GatewayTimeoutException.class);
	}

	public String getSolr(String url, String requestParam, Integer timeout, String message,
			Class<? extends Exception> clazz) throws GatewayTimeoutException {
		WebClient webClient = WebClient.builder().baseUrl(url).defaultHeaders(headers -> {
			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		}).build();

		try {
			ResponseEntity<String> jsonResponse = webClient.get()
					.uri(builder -> builder.path("/select").queryParam("q", requestParam).queryParam("rows", 5).build())
					.retrieve().toEntity(String.class).block();
			return jsonResponse.getBody();
		} catch (Exception e) {
			logger.error("FAILED TO SEND REQUEST: {}", e);
			if (e.getCause() instanceof TimeoutException) {
				throw new GatewayTimeoutException(message);
			} else {
				throw new RuntimeException(e);
			}
		}
	}
}

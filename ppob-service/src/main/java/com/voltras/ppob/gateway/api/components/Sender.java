package com.voltras.ppob.gateway.api.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
@Slf4j
public class Sender {
	@Value("${ppob.gateway.url}")
	private String url;

	public <T> T callRestService(String endPoint, HttpMethod method, Map<String, String> queryParams,
			Object requestBody, Map<String, String> headers, Class<T> responseType) throws WebClientResponseException {

		if (headers == null) {
			headers = new HashMap<>();
		}

		MultiValueMap<String, String> headerMap = convert(headers);

		HttpClient httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient", LogLevel.INFO,
				AdvancedByteBufFormat.TEXTUAL);

		WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(endPoint).build();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
		if (queryParams != null && !queryParams.isEmpty()) {
			queryParams.forEach(uriBuilder::queryParam);
		}
		String uriString = uriBuilder.build(false).toUriString();
		System.out.println(uriString);
		Mono<ResponseEntity<T>> responseMono;

		if (method == HttpMethod.POST) {
			WebClient.RequestBodySpec requestSpec = webClient.post().uri(uriString)
					.contentType(MediaType.APPLICATION_JSON).headers(h -> h.addAll(headerMap));

			if (requestBody == null) {
				responseMono = requestSpec.retrieve().toEntity(responseType);
			} else {
				responseMono = requestSpec.body(BodyInserters.fromValue(requestBody)).retrieve().toEntity(responseType);
			}
		} else {
			responseMono = webClient.get().uri(uriString).headers(h -> h.addAll(headerMap)).retrieve()
					.toEntity(responseType).doOnSubscribe(s -> {
						log.info("Sending request: {}", s);
					});
		}

		ResponseEntity<T> response = responseMono.onErrorResume(Exception.class, e -> {
			if (e instanceof WebClientResponseException we) {
				throw we;
			} else {
				throw new RuntimeException(e.getClass().toString() + e.getMessage());
			}
		}).block();

		T responseBody = response.getBody();

		return responseBody;
	}

	public MultiValueMap<String, String> convert(Map<String, String> map) {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			multiValueMap.add(entry.getKey(), entry.getValue());
		}
		return multiValueMap;
	}
}

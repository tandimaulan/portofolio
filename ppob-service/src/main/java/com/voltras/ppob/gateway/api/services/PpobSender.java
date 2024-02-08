package com.voltras.ppob.gateway.api.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltras.ppob.gateway.api.components.Sender;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PpobSender {

	@Autowired
	private Environment environment;

	@Autowired
	private Sender sender;

	private String getUrl() {
		return environment.getProperty("ppob.gateway.url");
	}

	private String getCid() {
		return environment.getProperty("ppob.gateway.cid");
	}

	public <T> T sendGet(Map<String, String> parameters, Class<T> returnClass) {
		try {
			if (parameters == null) {
				parameters = new HashMap<>();
			}
			logRequest(parameters);
			parameters.put("cid", getCid());
			JsonNode json = sender.callRestService(getUrl(), HttpMethod.GET, parameters, null, null, JsonNode.class);

			T ret = extractResponse(returnClass, json);

			logResponse(ret);
			return ret;
		} catch (WebClientResponseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private <T> T extractResponse(Class<T> returnClass, JsonNode json) {
		JsonNode errorNode = json.get("error_code");

		T ret = null;
		if (errorNode != null && !errorNode.isNull() && !errorNode.asText().equals("0")) {
			String errorCode = errorNode.asText();

			String errorMessage = null;

			JsonNode errorMessageNode = json.get("error_message");
			if (errorMessageNode != null && !errorMessageNode.isNull()) {
				errorMessage = errorMessageNode.asText();
			}
			throw new RuntimeException(errorCode);
		} else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				ret = mapper.treeToValue(json, returnClass);
			} catch (JsonProcessingException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
		return ret;
	}

	public <T> T sendPost(Map<String, String> parameters, Class<T> returnClass) {
		try {
			if (parameters == null) {
				parameters = new HashMap<>();
			}
			logRequest(parameters);
			parameters.put("cid", getCid());
			JsonNode json = sender.callRestService(getUrl(), HttpMethod.POST, parameters, null, null, JsonNode.class);

			T ret = extractResponse(returnClass, json);

			logResponse(ret);
			return ret;
		} catch (WebClientResponseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private <T> void logResponse(T ret) {
		try {
			log.info("""
					response :
					{}
					""", new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(ret));
		} catch (JsonProcessingException e) {
			log.warn("fail to write response log");
		}
	}

	private void logRequest(Map<String, String> parameters) {
		log.info("""
				sending ...
				parameters : parameters
				""", parameters);
	}
}

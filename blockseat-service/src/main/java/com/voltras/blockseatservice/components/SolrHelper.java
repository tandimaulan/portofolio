package com.voltras.blockseatservice.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltras.blockseat.api.enums.PersonType;
import com.voltras.blockseat.api.models.Airport;
import com.voltras.blockseat.api.models.Person;
import com.voltras.blockseatservice.entities.SolrPassengerResponse;
import com.voltras.blockseatservice.utils.JsonUtils;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;

@Component
public class SolrHelper {
	@Autowired
	private WebHelper webHelper;

	public Boolean checkInternational(String departureCode, String arrivalCode) {
		List<String> iatas = new ArrayList<String>();
		iatas.add(departureCode);
		iatas.add(arrivalCode);

		var response = searchAirportByList(iatas);
		return response.stream().anyMatch(airport -> !airport.country().trim().equalsIgnoreCase("Indonesia"));
	}

	public List<Airport> searchAirportByList(List<String> iataList) {
		String reqQuery = String.join(" | ",
				iataList.stream().map(iata -> String.format("IATA:*%s*", iata)).collect(Collectors.toList()));
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("q", reqQuery);
		return toSolr(params);
	}

	public List<Airport> toSolr(MultiValueMap<String, String> params) {
		List<Airport> result = new ArrayList<Airport>();
		String url = "http://172.16.10.92:8983/solr/sphinx-airport/select";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<MultiValueMap<String, String>> requestToSolr = new HttpEntity<>(params, headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestToSolr, String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode solrResponseBody;
		try {
			var root = mapper.readTree(responseEntity.getBody());
			solrResponseBody = root.path("response");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return result;
		}

		if (solrResponseBody == null) {
			return result;
		}

		solrResponseBody.path("docs").forEach(doc -> {
			result.add(new Airport(doc.get("IATA") != null ? doc.get("IATA").asText() : "",
					doc.get("name") != null ? doc.get("name").asText() : "",
					doc.get("city") != null ? doc.get("city").asText() : "",
					doc.get("country") != null ? doc.get("country").asText() : ""));
		});
		return result;
	}

	@Async
	public void savePassengerData(String officeId, Person data) {
		try {
			var docs = suggestPassengerData(officeId, data.firstName(), data.personType());
			docs = docs.stream().filter(doc -> doc.title().toString().equals(data.title().toString())
					&& doc.firstName().equalsIgnoreCase(data.firstName())).toList();
			if (!docs.isEmpty()) {
				return;
			}
			Map<String, Object> query = new LinkedHashMap<String, Object>();
			var fields = new HashMap<String, String>();
			fields.put("officeId", officeId);
			fields.put("TITLE", data.title().toString());
			fields.put("FIRSTNAME", data.firstName().toUpperCase());
			fields.put("LASTNAME", data.lastName().toUpperCase());

			query.put("add", Map.of("doc", fields));
			query.put("commit", Map.of());
			sendToSolr("solr.passenger.url=http://172.16.10.92:8983/solr/#/sphinx-passenger" + "/update/json/", query);
		} catch (JsonProcessingException | GatewayTimeoutException e) {
		}
	}

	public List<Person> suggestPassengerData(String officeCode, String autocomplete, PersonType type)
			throws JsonMappingException, JsonProcessingException, GatewayTimeoutException {
		var builder = new StringBuilder();
		builder.append("officeId:");
		builder.append(officeCode);
		builder.append(" AND autocomplete:*");
		builder.append(autocomplete);
		builder.append("*");

		var jsonResponse = webHelper.getSolr("solr.passenger.url=http://172.16.10.92:8983/solr/#/sphinx-passenger",
				builder.toString(), 30);
		var response = JsonUtils.readAsObject(jsonResponse, SolrPassengerResponse.class);
		return response.getResponse().getDocs();
	}

	private String sendToSolr(String url, Map<String, Object> query)
			throws JsonProcessingException, GatewayTimeoutException {
		var requestBody = JsonUtils.parseToString(query);
		return webHelper.post(url, requestBody, 30);
	}
}
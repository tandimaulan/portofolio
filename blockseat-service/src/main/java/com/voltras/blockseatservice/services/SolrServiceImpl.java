package com.voltras.blockseatservice.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltras.blockseat.admin.api.models.BlockseatSolrRequest;
import com.voltras.blockseat.admin.api.services.BlockseatSolrService;
import com.voltras.blockseat.api.models.Airport;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;

@Service("blockseatSolrService")
public class SolrServiceImpl implements RpcBasicService, BlockseatSolrService {

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<Airport> searchAirport(@Valid BlockseatSolrRequest request) {
		Double latitude = Double.valueOf("-6.2297419");
		if (request.latitude() != null) {
			latitude = request.latitude();
		}
//		AvailabilityRequest;
		Double longitude = Double.valueOf("106.759478");
		if (request.longitude() != null) {
			longitude = request.longitude();
		}
		BlockseatSolrRequest airportSolrRequest = new BlockseatSolrRequest(request.keyword(), latitude, longitude);
		List<Airport> responseList = new ArrayList<Airport>();
		String url = "http://172.16.10.92:8983/solr/sphinx-airport/select";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("q", "autocomplete:*" + airportSolrRequest.keyword() + "*");
		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<MultiValueMap<String, String>> requestToSolr = new HttpEntity<>(params, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestToSolr, String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(responseEntity.getBody());
			JsonNode solrResponseBody = root.path("response");
			if (solrResponseBody != null) {
				JsonNode solrDocs = solrResponseBody.path("docs");
				for (JsonNode doc : solrDocs) {
					String iata = "";
					String name = "";
					String city = "";
					String country = "";
					if (doc.get("IATA") != null) {
						iata = doc.get("IATA").asText();
					}
					if (doc.get("name") != null) {
						name = doc.get("name").asText();
					}
					if (doc.get("city") != null) {
						city = doc.get("city").asText();
					}
					if (doc.get("country") != null) {
						country = doc.get("country").asText();
					}
					Airport asr = new Airport(iata, name, city, country);
					responseList.add(asr);
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseList;
	}

	public List<Airport> searchAirportByList(List<String> iataList) {
		String requestQ = "";
		for (int i = 0; i < iataList.size(); i++) {
			if (i != 0)
				requestQ += " | ";
			requestQ += "IATA:*" + iataList.get(i) + "*";
		}

		List<Airport> result = new ArrayList<Airport>();
		String url = "http://172.16.10.92:8983/solr/sphinx-airport/select";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("q", requestQ);

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> requestToSolr = new HttpEntity<>(params, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestToSolr, String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(responseEntity.getBody());
			JsonNode solrResponseBody = root.path("response");
			if (solrResponseBody != null) {
				JsonNode solrDocs = solrResponseBody.path("docs");
				for (JsonNode doc : solrDocs) {
					String iata = "";
					String name = "";
					String city = "";
					String country = "";
					if (doc.get("IATA") != null) {
						iata = doc.get("IATA").asText();
					}
					if (doc.get("name") != null) {
						name = doc.get("name").asText();
					}
					if (doc.get("city") != null) {
						city = doc.get("city").asText();
					}
					if (doc.get("country") != null) {
						country = doc.get("country").asText();
					}
					Airport asr = new Airport(iata, name, city, country);
					result.add(asr);
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}

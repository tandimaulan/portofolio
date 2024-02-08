package com.voltras.blockseatservice.entities;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SolrPassengerResponse {
	private Map<String, Object> responseHeader;
	private ResponseInfo response;
}

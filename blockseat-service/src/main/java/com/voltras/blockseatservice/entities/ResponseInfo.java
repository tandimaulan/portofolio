package com.voltras.blockseatservice.entities;

import java.util.List;

import com.voltras.blockseat.api.models.Person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseInfo {
	private Integer numFound;
	private Integer start;
	private List<Person> docs;
}

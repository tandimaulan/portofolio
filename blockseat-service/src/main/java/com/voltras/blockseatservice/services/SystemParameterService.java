package com.voltras.blockseatservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseatservice.repositories.SystemParameterRepository;


@Service("systemParameter")
public class SystemParameterService {
	@Autowired
	private SystemParameterRepository repo;

	public String get(String key) {
		var systemParameter = repo.findByKey(key);
		if (systemParameter.isEmpty()) {
			return null;
		}
		return systemParameter.get().getValue();
	}
}

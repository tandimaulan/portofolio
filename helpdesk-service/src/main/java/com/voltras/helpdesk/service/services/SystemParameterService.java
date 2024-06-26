package com.voltras.helpdesk.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.helpdesk.service.repositories.SystemParameterRepository;

@Service
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

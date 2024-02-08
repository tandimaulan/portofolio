package com.voltras.ppob.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.ppob.service.entities.DomainData;
import com.voltras.ppob.service.repositories.DomainDataRepository;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Service
public class DomainService {
	@Autowired
	private VanAdditionalRequestData session;

	@Autowired
	private DomainDataRepository repo;

	public DomainData getDomain(String domainName) {
		return repo.findByCode(domainName == null ? session.getDomain() : domainName);
	}

	public Boolean getIsB2c(String domainName) {
		return getDomain(domainName).getIsB2c();
	}
}

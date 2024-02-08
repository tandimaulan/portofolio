package com.voltras.blockseatservice.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.voltras.blockseatservice.repositories.DomainDataRepository;
import com.voltras.blockseatservice.repositories.OfficeRepository;

public class DomainHelper {

	@Autowired
	private DomainDataRepository domainDataRepo;
	@Autowired
	private OfficeRepository officeRepo;
	
	public Map<String, Object> getAttributes(String domainName, String officeCode){
		var domain = domainDataRepo.findByName(domainName);
		var office = officeRepo.findByCode(officeCode).orElse(null);
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("officeCode", domain.getIsB2c() ? domain.getDefaultOfficeCode() : officeCode);
		attributes.put("officePackage", domain.getIsB2c() ? domain.getDefaultOfficePackage() : office.getPackageName());
		attributes.put("username", domain.getIsB2c() ? domain.getDefaultPrincipal() : office.getPrincipal());
		return attributes;
	}
}

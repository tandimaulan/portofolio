package com.voltras.ppob.service.components;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.ppob.utils.JsonUtils;
import com.voltras.voltrasspring.logging.AuditTrail;
import com.voltras.voltrasspring.logging.RequestTrail;
import com.voltras.voltrasspring.logging.ResponseTrail;
import com.voltras.voltrasspring.logging.VoltrasSpringLogging;

@Component
public class RequestLogging implements VoltrasSpringLogging {
	@Autowired
	LogHelper logger;

	@Override
	public AuditTrail logRequest(AuditTrail trail) {
		trail.setTrailId(UUID.randomUUID().toString());
		RequestTrail req = trail.getRequest();
		Object body = req == null ? null : req.getBody();
		try {

			body = body == null ? null : JsonUtils.parseToString(body);
		} catch (JsonProcessingException e) {
		}
		logger.info("REQUEST: {}", body);
		return trail;
	}

	@Override
	public AuditTrail logResponse(AuditTrail trail) {
		ResponseTrail rsp = trail.getResponse();
		Exception ex = rsp == null ? null : rsp.getError();
		if (ex == null) {
			Object body = rsp == null ? null : rsp.getBody();
			try {
				body = body == null ? null : JsonUtils.parseToString(body);
			} catch (JsonProcessingException e) {
			}
			logger.info("RESPONSE: {}", body);
		} else {
			logger.error("ERROR: {}", ex.getClass().getCanonicalName(), ex);
		}
		return trail;
	}

}

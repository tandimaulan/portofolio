package com.voltras.ppob.service.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.voltrasspring.exceptions.exceptions.VoltrasSpringRuntimeException;

@Component
public class LogHelper {
	private Logger infoLogger = LoggerFactory.getLogger("infos");
	private Logger errorLogger = LoggerFactory.getLogger("errors");

	public void info(String format, Object... args) {
		infoLogger.info(format, args);
	}

	public void error(String format, Object... args) {
		errorLogger.error(format, args);
	}
	
	public void throwDataNotFound(String className, String methodName, String entityName, String id)
			throws DataNotFoundException {
		error("[{}.{}] {} dengan id: {} tidak ditemukan", className, methodName, entityName, id);
		throw new DataNotFoundException();
	}

	public void throwRuntime(String className, String methodName, String message) {
		error("[{}.{}] {}", className, methodName, message);
		throw new VoltrasSpringRuntimeException(new RuntimeException(message));
	}
}


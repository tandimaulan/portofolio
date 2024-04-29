package com.voltras.helpdesk.service.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
}

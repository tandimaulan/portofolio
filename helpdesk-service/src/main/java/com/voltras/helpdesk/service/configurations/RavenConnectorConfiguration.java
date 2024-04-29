package com.voltras.helpdesk.service.configurations;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.voltras.kismisconnector.KismisConnector;

@Configuration
public class RavenConnectorConfiguration {
	@Value("${voltras.raven.url}")
	private String url;

	@Bean
	@Qualifier("ravenConnector")
	KismisConnector setupRavenConnector() {
		try {
			return new KismisConnector("ravenConnector", url);
		} catch (URISyntaxException e) {
			return null;
		}
	}
}

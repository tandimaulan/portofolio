package com.voltras.blockseatservice.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "blockseatlogs")
public class ActuatorLog {
	private final Map<String, String> logFiles = Map.of(
			"info", "logs/info.log", 
			"errors", "logs/errors.log", 
			"summary", "logs/summary.log");
	
	@ReadOperation
	public List<String> getLogs(@Selector String type) {
		String logFilePath = logFiles.get(type);
		if (logFilePath != null) {
			return readLogFile(logFilePath);
		} else {
			return Collections.singletonList("Invalid log type: " + type);
		}
	}

	private List<String> readLogFile(String logFilePath) {
		List<String> logs = new ArrayList<>();
		try {
			Process process = Runtime.getRuntime().exec("cat " + logFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] logParts = line.split("\n");
				for (String logPart : logParts) {
					logs.add(logPart.trim());
				}
			}
			process.waitFor();
			reader.close();
		} catch (IOException | InterruptedException e) {
			logs.add("Error fetching console logs: " + e.getMessage());
		}
		return logs;
	}
}
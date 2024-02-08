package com.voltras.ppob.service.configuration;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.voltras.ppob.service.services.SchedulerService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class AsyncSchedulerExecutor {
	private ExecutorService executorService;

	@PostConstruct
	private void create() {
		executorService = Executors.newCachedThreadPool();
	}

	public void submit(SchedulerService scheduler, Method method) {
		executorService.submit(() -> {
			try {
				method.invoke(scheduler);
			} catch (Exception e) {
				// just skip task if error occurred
			}
		});
	}

	@PreDestroy
	private void destroy() {
		executorService.shutdown();
	}
}
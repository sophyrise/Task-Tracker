package com.sophie.task_tracker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskTrackerApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate rest;

	@Test
	@DisplayName("Spring context boots")
	void contextLoads() {
		// If the context cannot start, this test will fail before assertions.
	}

	@Test
	@DisplayName("/v3/api-docs returns 200 and JSON when springdoc is compatible")
	void apiDocs_isAccessible() {
		ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/v3/api-docs", String.class);
		assert res.getStatusCode() == HttpStatus.OK : "Expected 200 from /v3/api-docs, got " + res.getStatusCode() +
			". This usually means springdoc version is incompatible with Spring (ControllerAdviceBean NoSuchMethodError).";
	}

	@Test
	@DisplayName("Swagger UI HTML is served")
	void swaggerUi_isServed() {
		ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/swagger-ui.html", String.class);
		assert res.getStatusCode() == HttpStatus.OK : "Swagger UI did not load. Check security config and springdoc version.";
	}
}

package com.accenture.assignment.holiday;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HolidayDetailServiceApplicationTests {

	@Autowired
	private RestTemplate restTemplate;

	@Test
	void contextLoads() {
	}
	@Test
	void restTemplateBeanIsLoaded() {
		assertThat(restTemplate).isNotNull();
	}
}

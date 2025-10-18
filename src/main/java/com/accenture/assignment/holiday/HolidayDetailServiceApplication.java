package com.accenture.assignment.holiday;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;


@SpringBootApplication
public class HolidayDetailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidayDetailServiceApplication.class, args);
	}

	@Bean
	public RestClient restClient(@Value("${nager.api.base-url:https://date.nager.at/api/v3}") String baseUrl) {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.build();
	}
}

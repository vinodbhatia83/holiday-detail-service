package com.accenture.assignment.holiday;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Main Spring Boot application class for the Holiday Detail Service.
 * <p>
 * This class configures and starts the Spring Boot application.
 * It also provides a {@link RestTemplate} bean with custom HTTP client settings.
 */
@SpringBootApplication
public class HolidayDetailServiceApplication {

	@Value("${holiday.http.connect-timeout:45}")
	private int connectTimeout;

	@Value("${holiday.http.response-timeout:30}")
	private int responseTimeout;

	/**
	 * Entry point for the Spring Boot application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HolidayDetailServiceApplication.class, args);
	}

	/**
	 * Creates a {@link RestTemplate} bean configured with custom connection and response timeouts.
	 * <p>
	 * The underlying HTTP client uses a 45-second connection timeout and a 30-second response timeout.
	 *
	 * @return a configured {@link RestTemplate} instance
	 */
	@Bean
	public RestTemplate restTemplate() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(Timeout.ofSeconds(connectTimeout))
				.setResponseTimeout(Timeout.ofSeconds(responseTimeout))
				.build();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(config)
				.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}
}
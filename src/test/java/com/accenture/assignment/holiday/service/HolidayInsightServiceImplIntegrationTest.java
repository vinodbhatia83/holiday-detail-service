package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HolidayInsightServiceImplIntegrationTest {

    @Autowired
    private HolidayInsightServiceImpl service;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private static final String BASE_API_URL = "https://date.nager.at/api/v3";
   // private static final String PUBLIC_HOLIDAYS_URI = "/PublicHolidays/{year}/{country}";

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    private void mockAvailableCountries() {
        String countriesJson = "[{\"countryCode\":\"AU\",\"name\":\"Australia\"},{\"countryCode\":\"AD\",\"name\":\"Andorra\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/AvailableCountries"))
                .andRespond(withSuccess(countriesJson, MediaType.APPLICATION_JSON));
        service.init();
    }

    @Test
    void testGetRecentHolidays() {
        String countriesJson = "[{\"countryCode\":\"AU\",\"name\":\"Australia\"},{\"countryCode\":\"AD\",\"name\":\"Andorra\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/AvailableCountries"))
                .andRespond(withSuccess(countriesJson, MediaType.APPLICATION_JSON));

        String holidaysJson = "[{\"date\":\"2024-01-01\",\"localName\":\"New Year\",\"name\":\"New Year\"}," +
                "{\"date\":\"2024-04-25\",\"localName\":\"ANZAC Day\",\"name\":\"ANZAC Day\"}," +
                "{\"date\":\"2024-12-25\",\"localName\":\"Christmas Day\",\"name\":\"Christmas Day\"}]";


        LocalDate now = LocalDate.now();
        int year = now.getYear();
        mockServer.expect(requestTo(BASE_API_URL + "/PublicHolidays/" + year + "/AU"))
                .andRespond(withSuccess(holidaysJson, MediaType.APPLICATION_JSON));

        service.init();
        List<Holiday> holidays = service.getRecentHolidays("AU");
        assertFalse(holidays.isEmpty());
        assertTrue(holidays.stream().anyMatch(h -> h.getName().equals("New Year")));
    }

    @Test
    void testGetNonWeekendHolidayCounts() {
        String countriesJson = "[{\"countryCode\":\"AU\",\"name\":\"Australia\"},{\"countryCode\":\"AD\",\"name\":\"Andorra\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/AvailableCountries"))
                .andRespond(withSuccess(countriesJson, MediaType.APPLICATION_JSON));

        String holidaysJson = "[{\"date\":\"2024-01-01\",\"localName\":\"New Year\",\"name\":\"New Year\"}," +
                "{\"date\":\"2024-01-06\",\"localName\":\"Epiphany\",\"name\":\"Epiphany\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/PublicHolidays/2024/AU"))
                .andRespond(withSuccess(holidaysJson, MediaType.APPLICATION_JSON));

        service.init();
        List<CountryHolidayCount> counts = service.getNonWeekendHolidayCounts(2024, "AU");
        assertEquals(1, counts.get(0).getCount());
    }

    @Test
    void testGetCommonHolidays() {
        String countriesJson = "[{\"countryCode\":\"AU\",\"name\":\"Australia\"},{\"countryCode\":\"AD\",\"name\":\"Andorra\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/AvailableCountries"))
                .andRespond(withSuccess(countriesJson, MediaType.APPLICATION_JSON));

        String holidaysAU = "[{\"date\":\"2024-01-01\",\"localName\":\"New Year\",\"name\":\"New Year\"}]";
        String holidaysAD = "[{\"date\":\"2024-01-01\",\"localName\":\"Any Nou\",\"name\":\"New Year\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/PublicHolidays/2024/AU"))
                .andRespond(withSuccess(holidaysAU, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(BASE_API_URL + "/PublicHolidays/2024/AD"))
                .andRespond(withSuccess(holidaysAD, MediaType.APPLICATION_JSON));

        service.init();
        List<CommonHoliday> result = service.getCommonHolidays(2024, "AU", "AD");
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());
    }

    @Test
    void testGetCommonHolidays_SameCountry_ThrowsException() {
        mockAvailableCountries();
        assertThrows(com.accenture.assignment.holiday.exception.InvalidCountryException.class,
                () -> service.getCommonHolidays(2024, "AU", "AU"));
    }
    @Test
    void testGetNonWeekendHolidayCounts_InvalidCountry() {
        String countriesJson = "[{\"countryCode\":\"AU\",\"name\":\"Australia\"}]";
        mockServer.expect(requestTo(BASE_API_URL + "/AvailableCountries"))
                .andRespond(withSuccess(countriesJson, MediaType.APPLICATION_JSON));

        service.init();
        List<CountryHolidayCount> counts = service.getNonWeekendHolidayCounts(2024, "ZZ");
        assertTrue(counts.isEmpty());
    }
}
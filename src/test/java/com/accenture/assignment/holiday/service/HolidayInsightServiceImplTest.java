package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.exception.ExternalApiUnavailableException;
import com.accenture.assignment.holiday.exception.InvalidCountryException;
import com.accenture.assignment.holiday.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HolidayInsightServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HolidayInsightServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Map<String, String> countries = new HashMap<>();
        countries.put("AU", "Australia");
        countries.put("AD", "Andorra");
        Field field = HolidayInsightServiceImpl.class.getDeclaredField("availableCountries");
        field.setAccessible(true);
        field.set(service, countries);
    }

    @Test
    void getRecentHolidays_ReturnsHolidays() {
        List<PublicHoliday> holidays = List.of(
                new PublicHoliday("2024-01-01","New Year","New Year"),
                new PublicHoliday("2024-04-25","ANZAC Day","ANZAC Day"));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), anyString()))
                .thenReturn(new ResponseEntity<>(holidays, HttpStatus.OK));

        List<Holiday> result = service.getRecentHolidays("AU");
        assertFalse(result.isEmpty(), "Result should not be empty");
        List<String> names = result.stream().map(Holiday::getName).toList();
        assertTrue(names.contains("New Year"));
        assertTrue(names.contains("ANZAC Day"));
    }

    @Test
    void getRecentHolidays_EmptyList() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), anyString()))
                .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));

        List<Holiday> result = service.getRecentHolidays("AU");
        assertTrue(result.isEmpty(), "Result should be empty for no holidays");
    }

    @Test
    void getNonWeekendHolidayCounts_ReturnsCounts() {
        List<PublicHoliday> holidays = List.of(
                new PublicHoliday("2024-01-01","New Year","New Year"),
                new PublicHoliday("2024-01-06","Epiphany","Epiphany"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), anyString()))
                .thenReturn(new ResponseEntity<>(holidays, HttpStatus.OK));

        List<CountryHolidayCount> result = service.getNonWeekendHolidayCounts(2024, "AU");
        assertEquals(1, result.get(0).getCount());
    }

    @Test
    void getNonWeekendHolidayCounts_InvalidCountry() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid country code"));

        List<CountryHolidayCount> result = service.getNonWeekendHolidayCounts(2024, "ZZ");
        assertTrue(result.isEmpty(), "Result should be empty for invalid country");
    }

    @Test
    void getCommonHolidays_ReturnsCommon() {
        List<PublicHoliday> holidaysAU = List.of(
                new PublicHoliday("2024-01-01","New Year","New Year"));
        List<PublicHoliday> holidaysAD = List.of(
                new PublicHoliday("2024-01-01","Any Nou","New Year"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), eq("AU")))
                .thenReturn(new ResponseEntity<>(holidaysAU, HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), anyInt(), eq("AD")))
                .thenReturn(new ResponseEntity<>(holidaysAD, HttpStatus.OK));

        List<CommonHoliday> result = service.getCommonHolidays(2024, "AU", "AD");
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());
    }

    @Test
    void getCommonHolidays_SameCountry_ThrowsException() {
        assertThrows(InvalidCountryException.class, () -> service.getCommonHolidays(2024, "AU", "AU"));
    }

    @Test
    void fetchAvailableCountriesFallback_ThrowsException() throws Exception {
        Field field = HolidayInsightServiceImpl.class.getDeclaredField("availableCountries");
        field.setAccessible(true);
        field.set(service, Collections.emptyMap());

        Method method = HolidayInsightServiceImpl.class.getDeclaredMethod("fetchAvailableCountriesFallback", RestClientException.class);
        method.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(service, new RestClientException("API down"));
        });
        assertTrue(ex.getCause() instanceof ExternalApiUnavailableException);
    }

    @Test
    void fetchAvailableCountriesFallback_ReturnsCache() throws Exception {
        // Set up a non-empty cache
        Map<String, String> cached = new HashMap<>();
        cached.put("AU", "Australia");
        Field field = HolidayInsightServiceImpl.class.getDeclaredField("availableCountries");
        field.setAccessible(true);
        field.set(service, cached);

        Method method = HolidayInsightServiceImpl.class.getDeclaredMethod("fetchAvailableCountriesFallback", RestClientException.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) method.invoke(service, new RestClientException("API down"));
        assertEquals(cached, result, "Should return cached countries");
    }
}
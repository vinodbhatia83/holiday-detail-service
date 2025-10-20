
package com.accenture.assignment.holiday.controller;

import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import com.accenture.assignment.holiday.service.HolidayInsightService;
import com.accenture.assignment.holiday.exception.InvalidCountryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidaysControllerTest {

    @Mock
    private HolidayInsightService service;

    @InjectMocks
    private HolidaysController controller;

    @Test
    void getRecentHolidays_returnsHolidayList() {
        List<Holiday> holidays = Arrays.asList(new Holiday(), new Holiday());
        when(service.getRecentHolidays("IN")).thenReturn(holidays);

        ResponseEntity<List<Holiday>> response = controller.getRecentHolidays("IN");

        assertEquals(holidays, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getRecentHolidays("IN");
    }

    @Test
    void getRecentHolidays_nullCountry_returnsEmptyList() {
        when(service.getRecentHolidays(null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Holiday>> response = controller.getRecentHolidays(null);

        assertEquals(Collections.emptyList(), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getRecentHolidays(null);
    }

    @Test
    void getRecentHolidays_serviceReturnsNull_returnsNullBody() {
        when(service.getRecentHolidays("IN")).thenReturn(null);

        ResponseEntity<List<Holiday>> response = controller.getRecentHolidays("IN");

        assertNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getRecentHolidays("IN");
    }

    @Test
    void getRecentHolidays_serviceThrowsException_propagatesException() {
        when(service.getRecentHolidays("ZZ")).thenThrow(new InvalidCountryException("Invalid country"));

        assertThrows(InvalidCountryException.class, () -> controller.getRecentHolidays("ZZ"));
        verify(service).getRecentHolidays("ZZ");
    }

    @Test
    void getNonWeekendHolidayCounts_returnsCountryHolidayCountList() {
        List<CountryHolidayCount> counts = Arrays.asList(new CountryHolidayCount(), new CountryHolidayCount());
        when(service.getNonWeekendHolidayCounts(2024, "IN,US")).thenReturn(counts);

        ResponseEntity<List<CountryHolidayCount>> response = controller.getNonWeekendHolidayCounts(2024, "IN,US");

        assertEquals(counts, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getNonWeekendHolidayCounts(2024, "IN,US");
    }

    @Test
    void getNonWeekendHolidayCounts_nullCountries_returnsEmptyList() {
        when(service.getNonWeekendHolidayCounts(2024, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CountryHolidayCount>> response = controller.getNonWeekendHolidayCounts(2024, null);

        assertEquals(Collections.emptyList(), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getNonWeekendHolidayCounts(2024, null);
    }

    @Test
    void getNonWeekendHolidayCounts_serviceReturnsNull_returnsNullBody() {
        when(service.getNonWeekendHolidayCounts(2024, "IN,US")).thenReturn(null);

        ResponseEntity<List<CountryHolidayCount>> response = controller.getNonWeekendHolidayCounts(2024, "IN,US");

        assertNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getNonWeekendHolidayCounts(2024, "IN,US");
    }

    @Test
    void getNonWeekendHolidayCounts_serviceThrowsException_propagatesException() {
        when(service.getNonWeekendHolidayCounts(2024, "ZZ")).thenThrow(new InvalidCountryException("Invalid country"));

        assertThrows(InvalidCountryException.class, () -> controller.getNonWeekendHolidayCounts(2024, "ZZ"));
        verify(service).getNonWeekendHolidayCounts(2024, "ZZ");
    }

    @Test
    void getCommonHolidays_returnsCommonHolidayList() {
        List<CommonHoliday> common = Arrays.asList(new CommonHoliday(), new CommonHoliday());
        when(service.getCommonHolidays(2024, "IN", "US")).thenReturn(common);

        ResponseEntity<List<CommonHoliday>> response = controller.getCommonHolidays(2024, "IN", "US");

        assertEquals(common, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getCommonHolidays(2024, "IN", "US");
    }

    @Test
    void getCommonHolidays_nullCountries_returnsEmptyList() {
        when(service.getCommonHolidays(2024, null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CommonHoliday>> response = controller.getCommonHolidays(2024, null, null);

        assertEquals(Collections.emptyList(), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getCommonHolidays(2024, null, null);
    }

    @Test
    void getCommonHolidays_serviceReturnsNull_returnsNullBody() {
        when(service.getCommonHolidays(2024, "IN", "US")).thenReturn(null);

        ResponseEntity<List<CommonHoliday>> response = controller.getCommonHolidays(2024, "IN", "US");

        assertNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(service).getCommonHolidays(2024, "IN", "US");
    }

    @Test
    void getCommonHolidays_serviceThrowsException_propagatesException() {
        when(service.getCommonHolidays(2024, "ZZ", "YY")).thenThrow(new InvalidCountryException("Invalid country"));

        assertThrows(InvalidCountryException.class, () -> controller.getCommonHolidays(2024, "ZZ", "YY"));
        verify(service).getCommonHolidays(2024, "ZZ", "YY");
    }
}
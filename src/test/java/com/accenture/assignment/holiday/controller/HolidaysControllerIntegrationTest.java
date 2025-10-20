
package com.accenture.assignment.holiday.controller;

import com.accenture.assignment.holiday.exception.InvalidCountryException;
import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.service.HolidayInsightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HolidaysControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HolidayInsightService service;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public HolidayInsightService holidayInsightService() {
            return mock(HolidayInsightService.class);
        }
    }


    @Test
    void getRecentHolidays_returnsEmptyList() throws Exception {
        when(service.getRecentHolidays("AD")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/holidays/recent")
                        .param("country", "AD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecentHolidays_missingCountryParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/holidays/recent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getNonWeekendHolidayCounts_returnsCountryHolidayCountList() throws Exception {
        List<CountryHolidayCount> counts = List.of(new CountryHolidayCount(), new CountryHolidayCount());
        when(service.getNonWeekendHolidayCounts(2024, "AD,AU")).thenReturn(counts);

        mockMvc.perform(get("/api/v1/holidays/non-weekend-count")
                        .param("year", "2024")
                        .param("countries", "AD,AU")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getNonWeekendHolidayCounts_returnsEmptyList() throws Exception {
        when(service.getNonWeekendHolidayCounts(2024, "AD,AU")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/holidays/non-weekend-count")
                        .param("year", "2024")
                        .param("countries", "AD,AU")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCommonHolidays_returnsCommonHolidayList() throws Exception {
        List<CommonHoliday> common = List.of(new CommonHoliday(), new CommonHoliday());
        when(service.getCommonHolidays(2025, "AD", "AU")).thenReturn(common);

        mockMvc.perform(get("/api/v1/holidays/common")
                        .param("year", "2025")
                        .param("country1", "AD")
                        .param("country2", "AU")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCommonHolidays_returnsEmptyList() throws Exception {
        when(service.getCommonHolidays(2025, "AD", "AU")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/holidays/common")
                        .param("year", "2025")
                        .param("country1", "AD")
                        .param("country2", "AU")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCommonHolidays_passingSameCountryParams_returnsBadRequest() throws Exception {
        when(service.getCommonHolidays(2025,"AD","AD")).thenThrow(new InvalidCountryException("Service error"));
        mockMvc.perform(get("/api/v1/holidays/common")
                        .param("year", "2025")
                        .param("country1", "AD")
                        .param("country2", "AD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommonHolidays_missingParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/holidays/common")
                        .param("year", "2025")
                        .param("country1", "AD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRecentHolidays_serviceThrowsException_returnsServerError() throws Exception {
        when(service.getRecentHolidays("AD")).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/v1/holidays/recent")
                        .param("country", "AD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getNonWeekendHolidayCounts_withDuplicateCountries_returnsCorrectList() throws Exception {
        List<CountryHolidayCount> counts = List.of(new CountryHolidayCount());
        when(service.getNonWeekendHolidayCounts(2024, "AD,AD")).thenReturn(counts);

        mockMvc.perform(get("/api/v1/holidays/non-weekend-count")
                        .param("year", "2024")
                        .param("countries", "AD,AD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
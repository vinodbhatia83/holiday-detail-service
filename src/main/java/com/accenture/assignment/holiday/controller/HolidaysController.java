package com.accenture.assignment.holiday.controller;

import com.accenture.assignment.holiday.api.HolidayApi;
import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import com.accenture.assignment.holiday.service.HolidayInsightService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for handling holiday-related API requests.
 * Implements {@link HolidayApi} to provide endpoints for retrieving recent holidays,
 * non-weekend holiday counts, and common holidays between countries.
 */
@RestController
@RequiredArgsConstructor
public class HolidaysController implements HolidayApi {

    private final HolidayInsightService service;

    /**
     * Retrieves the most recent holidays for the specified country.
     *
     * @param country the country code (e.g., "US", "IN")
     * @return a list of recent {@link Holiday} objects
     */
    @Override
    public ResponseEntity<List<Holiday>> getRecentHolidays(String country) {
        return ResponseEntity.ok(service.getRecentHolidays(country));
    }

    /**
     * Retrieves the count of non-weekend holidays for the given year and countries.
     *
     * @param year the year to filter holidays
     * @param countries comma-separated list of country codes
     * @return a list of {@link CountryHolidayCount} objects
     */
    @Override
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCounts(
            Integer year,
            String countries) {
        return ResponseEntity.ok(service.getNonWeekendHolidayCounts(year, countries));
    }

    /**
     * Retrieves the list of common holidays between two countries for a given year.
     *
     * @param year the year to filter holidays
     * @param country1 the first country code
     * @param country2 the second country code
     * @return a list of {@link CommonHoliday} objects
     */
    @Override
    public ResponseEntity<List<CommonHoliday>> getCommonHolidays(Integer year, String country1, String country2) {
        return ResponseEntity.ok(service.getCommonHolidays(year, country1, country2));
    }
}
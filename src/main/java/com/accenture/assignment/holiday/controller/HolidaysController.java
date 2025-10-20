package com.accenture.assignment.holiday.controller;

import com.accenture.assignment.holiday.api.HolidayApi;
import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import com.accenture.assignment.holiday.service.HolidayInsightService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HolidaysController implements HolidayApi {

    private final HolidayInsightService service;

    @Override
    public ResponseEntity<List<Holiday>> getRecentHolidays(String country) {
        return ResponseEntity.ok(service.getRecentHolidays(country));
    }

    @Override
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCounts(
            Integer year,
            String countries) {
        return ResponseEntity.ok(service.getNonWeekendHolidayCounts(year, countries));
    }


    @Override
    public ResponseEntity<List<CommonHoliday>> getCommonHolidays(Integer year, String country1, String country2) {
        return ResponseEntity.ok(service.getCommonHolidays(year, country1, country2));
    }
}
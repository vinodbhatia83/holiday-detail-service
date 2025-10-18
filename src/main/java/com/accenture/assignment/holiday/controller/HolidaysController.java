package com.accenture.assignment.holiday.controller;

import com.accenture.assignment.holiday.api.DefaultApi;

import com.accenture.assignment.holiday.model.*;
import com.accenture.assignment.holiday.service.HolidayInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;


@RestController
@Tag(name = " Holidays ")
@RequiredArgsConstructor
public class HolidaysController implements DefaultApi {

    private final HolidayInsightService service;

    @Override
    @Operation(summary = "Last 3 holidays ")
    public ResponseEntity<List<Holiday>> getRecentHolidays(@RequestParam String country) {
        return ResponseEntity.ok(service.getRecentHolidays(country));
    }


    @Override
    @Operation(summary = "Non -weekend counts")
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCounts(Integer year, String countries) {
        return ResponseEntity.ok(service.getNonWeekendHolidayCounts(year, countries));
    }

    @Override
    @Operation(summary = "Common holidays ")
    public ResponseEntity<List<CommonHoliday>> getCommonHolidays(Integer year, String
            country1, String country2) {
        return ResponseEntity.ok(service.getCommonHolidays(year, country1, country2));
    }
}
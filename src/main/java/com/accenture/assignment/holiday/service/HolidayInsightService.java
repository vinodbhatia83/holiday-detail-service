package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.model.*;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface HolidayInsightService {

    String AVAILABLE_COUNTRIES_URI = "/AvailableCountries";

    String PUBLIC_HOLIDAYS_URI = "/PublicHolidays/{year}/{country}";

    List<Holiday> getRecentHolidays(@NotBlank String country);

    List<CountryHolidayCount> getNonWeekendHolidayCounts(@NotNull Integer year, @NotBlank
    String countries);

    List<CommonHoliday> getCommonHolidays(@NotNull Integer year, @NotBlank String country1,
                                          @NotBlank String country2);
}
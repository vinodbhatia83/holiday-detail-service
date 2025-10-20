package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.model.*;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for retrieving and analyzing public holiday data across countries.
 * <p>
 * Provides methods to:
 * <ul>
 *   <li>Fetch recent holidays for a given country</li>
 *   <li>Count non-weekend holidays for multiple countries in a specific year</li>
 *   <li>Find holidays common to two countries in a given year</li>
 * </ul>
 * <p>
 * Uses constants for API endpoint URIs and base URL.
 * </p>
 *
 * @author vinodbhatia83
 */
public interface HolidayInsightService {

    String AVAILABLE_COUNTRIES_URI = "/AvailableCountries";

    String PUBLIC_HOLIDAYS_URI = "/PublicHolidays/{year}/{country}";

    String BASE_API_URL = "https://date.nager.at/api/v3";

    List<Holiday> getRecentHolidays(@NotBlank String country);

    List<CountryHolidayCount> getNonWeekendHolidayCounts(@NotNull Integer year, @NotBlank
    String countries);

    List<CommonHoliday> getCommonHolidays(@NotNull Integer year, @NotBlank String country1,
                                          @NotBlank String country2);
}
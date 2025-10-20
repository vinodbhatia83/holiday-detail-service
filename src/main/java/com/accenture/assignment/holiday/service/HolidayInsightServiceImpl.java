package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.exception.ExternalApiUnavailableException;
import com.accenture.assignment.holiday.exception.InvalidCountryException;
import com.accenture.assignment.holiday.model.AvailableCountry;
import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import com.accenture.assignment.holiday.model.PublicHoliday;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for providing insights into public holidays across countries.
 * <p>
 * This class interacts with an external API to fetch holiday data, caches results,
 * and offers various methods to analyze and compare holidays. It includes retry and
 * fallback mechanisms for improved resilience against API failures.
 * </p>
 *
 * <ul>
 *   <li>Fetches available countries and their codes from the external API.</li>
 *   <li>Retrieves public holidays for a given year and country, with caching and retry support.</li>
 *   <li>Provides recent holidays for a country.</li>
 *   <li>Counts non-weekend holidays for multiple countries.</li>
 *   <li>Finds common holidays between two countries.</li>
 * </ul>
 *
 * <p>
 * Exceptions:
 * <ul>
 *   <li>{@link com.accenture.assignment.holiday.exception.ExternalApiUnavailableException} - Thrown when the external API is unavailable and no cached data exists.</li>
 *   <li>{@link com.accenture.assignment.holiday.exception.InvalidCountryException} - Thrown when invalid country codes are provided.</li>
 * </ul>
 * </p>
 *
 * @author vinodbhatia83
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayInsightServiceImpl implements HolidayInsightService {

    private final RestTemplate restTemplate;

    private Map<String, String> availableCountries;
    private final Map<String, List<PublicHoliday>> holidaysCache = new HashMap<>();
    private static final int LOOK_BACK_YEARS = 10;
    private static final int RECENT_HOLIDAYS_COUNT = 3;

   /**
            * Initializes the available countries cache after bean construction.
            * <p>
            * This method is called automatically by Spring after dependency injection is complete.
            * It fetches the list of available countries from the external API and stores them in memory.
            * </p>
            */
    @PostConstruct
     void init() {
        availableCountries = fetchAvailableCountries();
    }

    @Retryable(
            value = RestClientException.class,
            maxAttemptsExpression = "${nager.api.retry.maxAttempts:3}",
            backoff = @org.springframework.retry.annotation.Backoff(delayExpression = "${nager.api.retry.delay:2000}")
    )
    private Map<String, String> fetchAvailableCountries() {
        String url = BASE_API_URL + AVAILABLE_COUNTRIES_URI;
        List<AvailableCountry> countries = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AvailableCountry>>() {}
        ).getBody();
        if (countries == null) return Collections.emptyMap();
        Map<String, String> result = countries.stream()
                .collect(Collectors.toMap(AvailableCountry::countryCode, AvailableCountry::name));
        availableCountries = result; // update cache
        return result;
    }

    @Recover
    private Map<String, String> fetchAvailableCountriesFallback(RestClientException e) {
        if (availableCountries != null && !availableCountries.isEmpty()) {
            return availableCountries;
        }
        log.warn("Failed to fetch available countries from external API, and no cached data is present.");
        throw new ExternalApiUnavailableException("Due to a temporary issue, we are unable to retrieve data from the external API. Please try again later.");
    }

    @Retryable(
            value = RestClientException.class,
            maxAttemptsExpression = "${nager.api.retry.maxAttempts:3}",
            backoff = @org.springframework.retry.annotation.Backoff(delayExpression = "${nager.api.retry.delay:2000}")
    )
    private List<PublicHoliday> getHolidays(int year, String country) {
        String countryCode = country.toUpperCase();
        if (!availableCountries.containsKey(countryCode)) {
            throw new IllegalArgumentException(
                    "Invalid country code: '" + country + "'. Please provide a valid country code. Here are some examples: " +
                            availableCountries.keySet().stream().limit(10).toList()
            );
        }
        String url = BASE_API_URL + PUBLIC_HOLIDAYS_URI;
        List<PublicHoliday> holidays = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PublicHoliday>>() {},
                year, countryCode
        ).getBody();
        if (holidays != null) {
            holidaysCache.put(countryCode + "-" + year, holidays); // update cache
        }
        return holidays;
    }

    @Recover
    private List<PublicHoliday> getHolidaysFallback(RestClientException e, int year, String country) {
        String countryCode = country.toUpperCase();
        List<PublicHoliday> cached = holidaysCache.get(countryCode + "-" + year);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        throw new ExternalApiUnavailableException("Due to a temporary issue, we are unable to retrieve data from the external API. Please try again later.");
    }

    private List<PublicHoliday> filterPastHolidays(List<PublicHoliday> holidays, LocalDate now) {
        return holidays.stream()
                .filter(h -> !LocalDate.parse(h.date()).isAfter(now))
                .sorted(Comparator.comparing(PublicHoliday::date).reversed())
                .toList();
    }

    @Override
    public List<Holiday> getRecentHolidays(String country) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        List<PublicHoliday> allPastHolidays = new ArrayList<>();
        int earliestYearToCheck = year - LOOK_BACK_YEARS;

        while (allPastHolidays.size() < RECENT_HOLIDAYS_COUNT && year > earliestYearToCheck) {
            List<PublicHoliday> holidays = getHolidays(year, country);
            if (holidays != null) {
                allPastHolidays.addAll(filterPastHolidays(holidays, now));
            }
            year--;
        }

        return allPastHolidays.stream()
                .limit(RECENT_HOLIDAYS_COUNT)
                .map(h -> new Holiday(LocalDate.parse(h.date()), h.name()))
                .toList();
    }

    @Override
    public List<CountryHolidayCount> getNonWeekendHolidayCounts(Integer year, String countries) {

        Set<String> countrySet = Arrays.stream(countries.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        return countrySet.stream()
                .map(country -> {
                    try {
                        List<PublicHoliday> holidays = getHolidays(year, country);
                        if (holidays == null) return null;
                        long count = holidays.stream()
                                .map(h -> LocalDate.parse(h.date()))
                                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                                .count();
                        return new CountryHolidayCount(country.toUpperCase(), (int) count);
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CountryHolidayCount::getCount).reversed())
                .toList();
    }
    /**
            * Finds holidays that are common between two countries for a given year.
 * <p>
 * Compares public holidays for both countries and returns a list of holidays
 * that occur on the same date in both countries, including their local names.
            * </p>
            *
            * @param year      the year for which to compare holidays
 * @param country1  the first country code
 * @param country2  the second country code
 * @return a list of {@link CommonHoliday} objects representing holidays shared by both countries
 * @throws InvalidCountryException if both country codes are the same
 */
    @Override
    public List<CommonHoliday> getCommonHolidays(Integer year, String country1, String country2) {
        if (country1 != null && country1.equalsIgnoreCase(country2)) {
            throw new InvalidCountryException("country1 and country2 must be different.");
        }
        List<PublicHoliday> holidays1 = getHolidays(year, country1);
        List<PublicHoliday> holidays2 = getHolidays(year, country2);

        if (holidays1 == null || holidays2 == null) return Collections.emptyList();
        Map<String, List<String>> map1 = holidays1.stream()
                .collect(Collectors.groupingBy(
                        PublicHoliday::date,
                        Collectors.mapping(PublicHoliday::localName, Collectors.toList())
                ));
        Map<String, List<String>> map2 = holidays2.stream()
                .collect(Collectors.groupingBy(
                        PublicHoliday::date,
                        Collectors.mapping(PublicHoliday::localName, Collectors.toList())
                ));

        return map1.keySet().stream()
                .filter(map2::containsKey)
                .sorted()
                .map(date -> new CommonHoliday(
                        LocalDate.parse(date),
                        String.join(", ", map1.get(date)),
                        String.join(", ", map2.get(date))
                ))
                .toList();
    }
}
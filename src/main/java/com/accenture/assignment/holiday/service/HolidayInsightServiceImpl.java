package com.accenture.assignment.holiday.service;

import com.accenture.assignment.holiday.model.AvailableCountry;
import com.accenture.assignment.holiday.model.CommonHoliday;
import com.accenture.assignment.holiday.model.CountryHolidayCount;
import com.accenture.assignment.holiday.model.Holiday;
import com.accenture.assignment.holiday.model.PublicHoliday;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class HolidayInsightServiceImpl implements HolidayInsightService {

    private final RestClient restClient;
    private Map<String, String> availableCountries;
    private static final int LOOK_BACK_YEARS = 10;
    private static final int RECENT_HOLIDAYS_COUNT = 3;

    @PostConstruct
    public void init() {
        availableCountries = fetchAvailableCountries();
    }

    private Map<String, String> fetchAvailableCountries() {
        List<AvailableCountry> countries = restClient.get()
                    .uri(AVAILABLE_COUNTRIES_URI)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AvailableCountry>>() {});
            if (countries == null) return Collections.emptyMap();
            return countries.stream()
                    .collect(Collectors.toMap(AvailableCountry::countryCode, AvailableCountry::name));

    }

    private List<PublicHoliday> getHolidays(int year, String country) {
        String countryCode = country.toUpperCase();
        if (!availableCountries.containsKey(countryCode)) {
            throw new IllegalArgumentException("Invalid country code: " + country + ". Sending a few Available countries code as example: " + availableCountries.keySet().stream().limit(10).toList());
        }
            return restClient.get()
                    .uri(PUBLIC_HOLIDAYS_URI, year, countryCode)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PublicHoliday>>() {});

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
        if (countries == null || countries.isEmpty()) return Collections.emptyList();

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

    @Override
    public List<CommonHoliday> getCommonHolidays(Integer year, String country1, String country2) {

            List<PublicHoliday> holidays1 = getHolidays(year, country1);
            List<PublicHoliday> holidays2 = getHolidays(year, country2);

            if (holidays1 == null || holidays2 == null) return Collections.emptyList();

            Map<String, String> map1 = holidays1.stream()
                    .collect(Collectors.toMap(PublicHoliday::date, PublicHoliday::localName));
            Map<String, String> map2 = holidays2.stream()
                    .collect(Collectors.toMap(PublicHoliday::date, PublicHoliday::localName));

            return map1.keySet().stream()
                    .filter(map2::containsKey)
                    .sorted()
                    .map(date -> new CommonHoliday(LocalDate.parse(date), map1.get(date), map2.get(date)))
                    .toList();

    }
}
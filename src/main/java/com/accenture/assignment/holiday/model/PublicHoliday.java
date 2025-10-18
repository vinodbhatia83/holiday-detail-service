package com.accenture.assignment.holiday.model;

import java.util.Set;

public record PublicHoliday (

        String date,
        String localName,
        String name,
        String countryCode,
        boolean fixed,
        boolean global,
        Set<String> counties,
        int launchYear,
        Set<String>types

){}
package com.accenture.assignment.holiday.model;

import lombok.Builder;

import java.util.Set;


public record PublicHoliday (

        String date,
        String localName,
        String name

){}
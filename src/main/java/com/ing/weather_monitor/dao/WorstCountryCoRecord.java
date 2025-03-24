package com.ing.weather_monitor.dao;

import org.springframework.data.relational.core.mapping.Column;

public record WorstCountryCoRecord(
        @Column("country_name")
        String countryName,

        @Column("city_count")
        int cityCount
) {}

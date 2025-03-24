package com.ing.weather_monitor.dao;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

public record WorstCityNo2Y2YRecord(
        @Column("city_name")
        String cityName,

        @Column("country_name")
        String countryName,

        @Column("avg_no2_current")
        BigDecimal avgNo2Current,

        @Column("avg_no2_year_before")
        BigDecimal avgNo2YearBefore
) {}

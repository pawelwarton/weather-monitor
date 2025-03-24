package com.ing.weather_monitor.dao;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

public record WorstCityPm10Record(
        @Column("city_name")
        String cityName,

        @Column("region_name")
        String regionName,

        @Column("avg_pm10")
        BigDecimal avgPm10
) {}

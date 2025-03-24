package com.ing.weather_monitor.dao;

import org.springframework.data.relational.core.mapping.Column;

public record RisingCoOrPm10CityRecord(
        @Column("city_id")
        String cityId,

        @Column("city_name")
        String cityName,

        @Column("rising_co")
        boolean risingCo,

        @Column("rising_pm10")
        boolean risingPm10
) {}

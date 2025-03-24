package com.ing.weather_monitor.dao;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

public record Last1HStatsPerCityRecord(
        @Column("avg_no2_last_hour")
        BigDecimal avgNO2LastHour,

        @Column("max_no2_last_hour")
        BigDecimal maxNO2LastHour,

        @Column("min_no2_last_hour")
        BigDecimal minNO2LastHour,

        @Column("avg_co_last_hour")
        BigDecimal avgCOLastHour,

        @Column("max_co_last_hour")
        BigDecimal maxCOLastHour,

        @Column("min_co_last_hour")
        BigDecimal minCOLastHour,

        @Column("avg_pm10_last_hour")
        BigDecimal avgPM10LastHour,

        @Column("max_pm10_last_hour")
        BigDecimal maxPM10LastHour,

        @Column("min_pm10_last_hour")
        BigDecimal minPM10LastHour
) {}

package com.ing.weather_monitor.dao;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("measures")
@Builder
public record MeasureEntity(
        @Id
        @Column("id")
        long id,

        @Column("sensor_id")
        String sensorId,

        @Column("city_id")
        String cityId,

        @Column("pm10")
        BigDecimal pm10,

        @Column("co")
        BigDecimal co,

        @Column("no2")
        BigDecimal no2,

        @Column("timestamp")
        Instant timestamp
) {}

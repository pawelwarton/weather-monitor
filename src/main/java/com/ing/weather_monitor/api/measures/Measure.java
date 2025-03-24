package com.ing.weather_monitor.api.measures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @param sensorId  unikalny identyfikator stacji pomiarowej
 * @param cityId    unikalny identyfikator miejscowości w której znajduje się stacja wykonująca ten pomiar
 * @param pm10      wskaźnik pyłu zawieszonego PM10
 * @param co        wskaźnik tlenku węgla
 * @param no2       wskaźnik dwutlenku azotu
 * @param timestamp data i czas (UTC) wykonania pomiaru zaprezentowana jako ilość sekund, która minęła od 1 stycznia 1970 00:00:00
 */
@Builder
public record Measure(
        @JsonProperty("sensorId")
        String sensorId,

        @JsonProperty("cityId")
        String cityId,

        @JsonProperty("PM10")
        BigDecimal pm10,

        @JsonProperty("CO")
        BigDecimal co,

        @JsonProperty("NO2")
        BigDecimal no2,

        @JsonProperty("timestamp")
        Instant timestamp
) {}

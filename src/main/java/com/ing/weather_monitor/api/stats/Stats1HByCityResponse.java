package com.ing.weather_monitor.api.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Stats1HByCityResponse(
        @JsonProperty("avgNO2LastHour")
        String avgNO2LastHour,

        @JsonProperty("maxNO2LastHour")
        String maxNO2LastHour,

        @JsonProperty("minNO2LastHour")
        String minNO2LastHour,

        @JsonProperty("avgCOLastHour")
        String avgCOLastHour,

        @JsonProperty("maxCOLastHour")
        String maxCOLastHour,

        @JsonProperty("minCOLastHour")
        String minCOLastHour,

        @JsonProperty("avgPM10LastHour")
        String avgPM10LastHour,

        @JsonProperty("maxPM10LastHour")
        String maxPM10LastHour,

        @JsonProperty("minPM10LastHour")
        String minPM10LastHour
) {}

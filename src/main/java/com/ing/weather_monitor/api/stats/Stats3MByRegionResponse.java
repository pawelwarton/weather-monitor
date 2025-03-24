package com.ing.weather_monitor.api.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record Stats3MByRegionResponse(
        @JsonProperty("risingCO3MCities")
        List<String> risingCO3MCities,

        @JsonProperty("risingPM103MCities")
        List<String> risingPM103MCities
) {}

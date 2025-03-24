package com.ing.weather_monitor.external.eu_cities_dictionary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record City(
        @JsonProperty("country")
        String country,
        @JsonProperty("city")
        String city,
        @JsonProperty("region")
        String region,
        @JsonProperty("regionId")
        String regionId
) {}

package com.ing.weather_monitor.external.eu_cities_dictionary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param lastUpdate data i czas (yyyyMMdd HH:mm:ss) ostatniej aktualizacji tych danych
 * @param cities     ista wszystkich dostępnych miejscowości w których zainstalowane są jakiekolwiek stacje pomiaru
 */
@Builder
public record Cities(
        @JsonProperty("lastUpdate")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastUpdate,

        @JsonProperty("cities")
        List<City> cities
) {
    @Builder
    public record City(
            @JsonProperty("country")
            String country,
            @JsonProperty("city")
            String city,
            @JsonProperty("cityId")
            String cityId,
            @JsonProperty("region")
            String region,
            @JsonProperty("regionId")
            String regionId
    ) {}
}

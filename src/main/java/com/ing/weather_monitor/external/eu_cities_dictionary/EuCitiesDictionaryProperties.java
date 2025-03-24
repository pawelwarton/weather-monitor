package com.ing.weather_monitor.external.eu_cities_dictionary;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.eu-cities-dictionary")
public record EuCitiesDictionaryProperties(
        String baseUrl
) {}

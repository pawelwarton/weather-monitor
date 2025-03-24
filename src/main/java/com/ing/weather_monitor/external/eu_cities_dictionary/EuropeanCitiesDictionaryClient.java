package com.ing.weather_monitor.external.eu_cities_dictionary;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface EuropeanCitiesDictionaryClient {

    @GetExchange("/cities/{cityId}")
    City getCityById(@PathVariable("cityId") String cityId);

    @GetExchange("/cities")
    Cities getCities();
}


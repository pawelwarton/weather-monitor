package com.ing.weather_monitor.external.eu_cities_dictionary;

import com.ing.weather_monitor.external.eu_cities_dictionary.Cities;
import com.ing.weather_monitor.external.eu_cities_dictionary.City;
import com.ing.weather_monitor.external.eu_cities_dictionary.EuCitiesConfig;
import com.ing.weather_monitor.external.eu_cities_dictionary.EuropeanCitiesDictionaryClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(EuCitiesConfig.class)
@TestPropertySource(properties = "app.eu-cities-dictionary.base-url=https://api.europeancitiesdictionary.info")
class EuropeanCitiesDictionaryClientTest {

    @Autowired
    MockRestServiceServer mockRestServiceServer;

    @Autowired
    EuropeanCitiesDictionaryClient client;

    @Test
    void shouldReceiveCitiesResponse() {
        var examplePayload = """
                {
                    "lastUpdate": "2025-02-27 08:23:44",
                    "cities": [
                        {
                            "country": "Poland",
                            "city": "Cieszyn",
                            "cityId": "a0c492f2-8b96-1e7f-8a7a-286866d3fa77",
                            "region": "śląskie",
                            "regionId": "a04295f2-8d96-4a7d-8a7a-286866d3fa22"
                        },
                        {
                            "country": "Poland",
                            "city": "Ruda Śląska",
                            "cityId": "c8f40572-6dd1-4bba-a1a0-a897442b85a1",
                            "region": "śląskie",
                            "regionId": "ffa525f2-d996-3z1d-3a7c-316866d3fa2ae"
                        }
                    ]
                }
                """;
        var expectedCities = Cities.builder()
                .lastUpdate(LocalDateTime.parse("2025-02-27T08:23:44"))
                .cities(List.of(
                        Cities.City.builder()
                                .country("Poland")
                                .city("Cieszyn")
                                .cityId("a0c492f2-8b96-1e7f-8a7a-286866d3fa77")
                                .region("śląskie")
                                .regionId("a04295f2-8d96-4a7d-8a7a-286866d3fa22")
                                .build(),
                        Cities.City.builder()
                                .country("Poland")
                                .city("Ruda Śląska")
                                .cityId("c8f40572-6dd1-4bba-a1a0-a897442b85a1")
                                .region("śląskie")
                                .regionId("ffa525f2-d996-3z1d-3a7c-316866d3fa2ae")
                                .build()
                ))
                .build();

        mockRestServiceServer.expect(requestTo("https://api.europeancitiesdictionary.info/cities"))
                .andRespond(withSuccess(examplePayload, MediaType.APPLICATION_JSON));

        var cities = client.getCities();

        assertThat(cities).isEqualTo(expectedCities);
    }

    @Test
    void shouldReceiveCityResponse() {
        var examplePayload = """
                {
                    "country": "Poland",
                    "city": "Ruda Śląska",
                    "region": "śląskie",
                    "regionId": "f0b295f2-8d96-4a7d-8a7a-286866d3fa22"
                }
                """;
        var expectedCity = City.builder()
                .country("Poland")
                .city("Ruda Śląska")
                .region("śląskie")
                .regionId("f0b295f2-8d96-4a7d-8a7a-286866d3fa22")
                .build();
        var cityId = "6587d520-577c-4263-b760-0c38745c2592";

        mockRestServiceServer.expect(requestTo("https://api.europeancitiesdictionary.info/cities/6587d520-577c-4263-b760-0c38745c2592"))
                .andRespond(withSuccess(examplePayload, MediaType.APPLICATION_JSON));

        var city = client.getCityById(cityId);

        assertThat(city).isEqualTo(expectedCity);
    }
}
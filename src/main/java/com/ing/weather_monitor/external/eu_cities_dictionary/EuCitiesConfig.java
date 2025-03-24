package com.ing.weather_monitor.external.eu_cities_dictionary;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties({EuCitiesDictionaryProperties.class})
class EuCitiesConfig {

    @Bean
    EuropeanCitiesDictionaryClient europeanCitiesDictionaryClient(
            RestClient.Builder builder,
            EuCitiesDictionaryProperties properties
    ) {
        RestClient restClient = builder
                .baseUrl(properties.baseUrl())
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();
        return factory.createClient(EuropeanCitiesDictionaryClient.class);
    }
}

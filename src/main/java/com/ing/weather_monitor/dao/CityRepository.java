package com.ing.weather_monitor.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
@RequiredArgsConstructor
public class CityRepository {
    private final JdbcAggregateTemplate template;

    public void save(CityEntity city) {
        Assert.notNull(city.id(), "city.id must not be null");

        template.insert(city);
    }

    public boolean existsById(String cityId) {
        return template.existsById(cityId, CityEntity.class);
    }
}

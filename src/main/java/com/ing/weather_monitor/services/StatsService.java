package com.ing.weather_monitor.services;

import com.ing.weather_monitor.api.stats.Stats1HByCityResponse;
import com.ing.weather_monitor.api.stats.Stats3MByRegionResponse;
import com.ing.weather_monitor.dao.MeasureRepository;
import com.ing.weather_monitor.mappers.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsMapper mapper;
    private final MeasureRepository measureRepository;

    public Stats3MByRegionResponse getStats3MByRegion(String regionId) {
        var risingCoOrPm10Cities = measureRepository.findRisingCoOrPm103mCitiesByRegionId(regionId);

        var risingCo3mCities = new ArrayList<String>();
        var risingPm103mCities = new ArrayList<String>();

        for (var record : risingCoOrPm10Cities) {
            var cityName = record.cityName();
            if (record.risingCo()) {
                risingCo3mCities.add(cityName);
            }
            if (record.risingPm10()) {
                risingPm103mCities.add(cityName);
            }
        }

        return Stats3MByRegionResponse.builder()
                .risingCO3MCities(risingCo3mCities)
                .risingPM103MCities(risingPm103mCities)
                .build();
    }

    public Stats1HByCityResponse getStats1HByCity(String cityId) {
        var stats = measureRepository.findLast1HStatsByCity(cityId);
        return mapper.toResponse(stats);
    }
}

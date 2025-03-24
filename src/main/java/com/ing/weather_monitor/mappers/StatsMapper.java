package com.ing.weather_monitor.mappers;

import com.ing.weather_monitor.api.stats.Stats1HByCityResponse;
import com.ing.weather_monitor.dao.Last1HStatsPerCityRecord;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ERROR
)
public interface StatsMapper {

    Stats1HByCityResponse toResponse(Last1HStatsPerCityRecord src);
}

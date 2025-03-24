package com.ing.weather_monitor.mappers;

import com.ing.weather_monitor.api.measures.Measure;
import com.ing.weather_monitor.dao.MeasureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ERROR
)
public interface MeasureMapper {

    @Mapping(target = "id", ignore = true)
    MeasureEntity toEntity(Measure measure);
}

package com.ing.weather_monitor.mappers;

import com.ing.weather_monitor.dao.CityEntity;
import com.ing.weather_monitor.external.eu_cities_dictionary.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ERROR
)
public interface CityMapper {

    @Mapping(target = "id", source = "cityId")
    @Mapping(target = "name", source = "source.city")
    @Mapping(target = "regionName", source = "source.region")
    @Mapping(target = "countryName", source = "source.country")
    CityEntity toEntity(String cityId, City source);
}

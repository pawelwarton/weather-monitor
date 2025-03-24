package com.ing.weather_monitor.dao;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cities")
@Builder
public record CityEntity(
        @Id
        @Column("id")
        String id,
        @Column("name")
        String name,

        // TODO: separate table for region?
        @Column("region_id")
        String regionId,
        @Column("region_name")
        String regionName,

        @Column("country_name")
        String countryName
) {}

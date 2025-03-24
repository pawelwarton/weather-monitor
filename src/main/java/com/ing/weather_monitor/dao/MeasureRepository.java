package com.ing.weather_monitor.dao;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasureRepository extends CrudRepository<MeasureEntity, Long> {

    @Query("SELECT city_name, region_name, avg_pm10 FROM worst_cities_pm10")
    List<WorstCityPm10Record> findWorstCitiesPm10();

    @Query("SELECT country_name, city_count FROM worst_countries_co")
    List<WorstCountryCoRecord> findWorstCountriesCo();

    @Query("SELECT city_name, country_name, avg_no2_current, avg_no2_year_before FROM worst_cities_no2_y2y")
    List<WorstCityNo2Y2YRecord> findWorstCitiesNo2Y2();

    @Query("""
            SELECT city_id, city_name, rising_co, rising_pm10
            FROM rising_co_or_pm10_3m_cities
            WHERE region_id = :regionId
            """)
    List<RisingCoOrPm10CityRecord> findRisingCoOrPm103mCitiesByRegionId(@Param("regionId") String regionId);

    @Query("""
            SELECT min_no2_last_hour, avg_no2_last_hour, max_no2_last_hour,
                   min_co_last_hour, avg_co_last_hour, max_co_last_hour,
                   min_pm10_last_hour, avg_pm10_last_hour, max_pm10_last_hour
            FROM last_hour
            WHERE city_id = :cityId
            """)
    Last1HStatsPerCityRecord findLast1HStatsByCity(@Param("cityId") String cityId);

}

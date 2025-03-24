package com.ing.weather_monitor.integration;

import com.ing.weather_monitor.api.stats.Stats1HByCityResponse;
import com.ing.weather_monitor.api.stats.Stats3MByRegionResponse;
import com.ing.weather_monitor.dao.MeasureRepository;
import com.ing.weather_monitor.external.eu_cities_dictionary.City;
import com.ing.weather_monitor.external.eu_cities_dictionary.EuropeanCitiesDictionaryClient;
import com.ing.weather_monitor.tasks.ReportGenerator;
import com.ing.weather_monitor.tasks.ReportProperties;
import com.ing.weather_monitor.util.MeasureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ing.weather_monitor.util.MeasureUtil.measureBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "app.report.output-dir=test-raporty")
class WeatherMonitorApplicationTests {

    @MockitoBean
    EuropeanCitiesDictionaryClient europeanCitiesDictionaryClient;

    @Autowired
    MeasureRepository measureRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ReportProperties reportProperties;

    @Autowired
    ReportGenerator reportGenerator;

    @BeforeEach
    void init() throws Exception {
        measureRepository.deleteAll();

        List<Path> reportFiles;
        try (var stream = Files.list(reportProperties.outputDir())) {
            reportFiles = stream.toList();
        }
        for (var file : reportFiles) {
            Files.delete(file);
        }
    }

    @Test
    void shouldSaveMeasure() {
        postMeasure("""
                {
                    "sensorId": "2a83e44f-55c5-480e-a5ee-2bab5c04a597",
                    "cityId": "75d6753b-5f93-4db8-a9cd-506b6115b93d",
                    "PM10": "23.1",
                    "CO": "12.4",
                    "NO2": "0.39",
                    "timestamp": 1742332375
                }
                """);

        assertThat(measureRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(measure -> {
                    assertThat(measure.sensorId()).isEqualTo("2a83e44f-55c5-480e-a5ee-2bab5c04a597");
                    assertThat(measure.cityId()).isEqualTo("75d6753b-5f93-4db8-a9cd-506b6115b93d");
                    assertThat(measure.pm10()).isEqualTo("23.1");
                    assertThat(measure.co()).isEqualTo("12.4");
                    assertThat(measure.no2()).isEqualTo("0.39");
                    assertThat(measure.timestamp().toEpochMilli()).isEqualTo(1742332375L * 1000);
                });
    }

    @Test
    void shouldComputeLast1HStats() {
        when(europeanCitiesDictionaryClient.getCityById("75d6753b-5f93-4db8-a9cd-506b6115b93d")).thenReturn(City.builder()
                .city("test-city-1-1-1")
                .regionId("63c8db85-418c-4b34-9b6a-ff9a7e33434b")
                .region("test-region-1-1")
                .country("test-country-1")
                .build());
        when(europeanCitiesDictionaryClient.getCityById("8a736af6-47b3-4b7e-a056-2c4c68f4cd19")).thenReturn(City.builder()
                .city("test-city-1-1-2")
                .regionId("63c8db85-418c-4b34-9b6a-ff9a7e33434b")
                .region("test-region-1-1")
                .country("test-country-1")
                .build());

        // same city, older than 1 hour
        postMeasure("""
                {
                    "sensorId": "2a83e44f-55c5-480e-a5ee-2bab5c04a597",
                    "cityId": "75d6753b-5f93-4db8-a9cd-506b6115b93d",
                    "PM10": "1",
                    "CO": "2",
                    "NO2": "3",
                    "timestamp": %d
                }
                """.formatted(ZonedDateTime.now().minusHours(2).toEpochSecond()));

        // same city
        postMeasure("""
                {
                    "sensorId": "2a83e44f-55c5-480e-a5ee-2bab5c04a597",
                    "cityId": "75d6753b-5f93-4db8-a9cd-506b6115b93d",
                    "PM10": "2",
                    "CO": "4",
                    "NO2": "6",
                    "timestamp": %d
                }
                """.formatted(ZonedDateTime.now().minusMinutes(45).toEpochSecond()));

        // same city
        postMeasure("""
                {
                    "sensorId": "2a83e44f-55c5-480e-a5ee-2bab5c04a597",
                    "cityId": "75d6753b-5f93-4db8-a9cd-506b6115b93d",
                    "PM10": "3",
                    "CO": "6",
                    "NO2": "9",
                    "timestamp": %d
                }
                """.formatted(ZonedDateTime.now().minusMinutes(10).toEpochSecond()));

        // other city
        postMeasure("""
                {
                    "sensorId": "53fe84fb-112a-4555-8fbb-cd85421e76cb",
                    "cityId": "8a736af6-47b3-4b7e-a056-2c4c68f4cd19",
                    "PM10": "4",
                    "CO": "8",
                    "NO2": "12",
                    "timestamp": %d
                }
                """.formatted(ZonedDateTime.now().minusMinutes(5).toEpochSecond()));

        var stats = get1HStatsByCity("75d6753b-5f93-4db8-a9cd-506b6115b93d");

        assertThat(stats.minPM10LastHour()).asDouble().isCloseTo(2.0, withinPercentage(1));
        assertThat(stats.avgPM10LastHour()).asDouble().isCloseTo(2.5, withinPercentage(1));
        assertThat(stats.maxPM10LastHour()).asDouble().isCloseTo(3.0, withinPercentage(1));

        assertThat(stats.minCOLastHour()).asDouble().isCloseTo(4.0, withinPercentage(1));
        assertThat(stats.avgCOLastHour()).asDouble().isCloseTo(5.0, withinPercentage(1));
        assertThat(stats.maxCOLastHour()).asDouble().isCloseTo(6.0, withinPercentage(1));

        assertThat(stats.minNO2LastHour()).asDouble().isCloseTo(6.0, withinPercentage(1));
        assertThat(stats.avgNO2LastHour()).asDouble().isCloseTo(7.5, withinPercentage(1));
        assertThat(stats.maxNO2LastHour()).asDouble().isCloseTo(9.0, withinPercentage(1));
    }

    @Test
    void shouldCalculate3MStatsByRegion() {
        var countryName = "test-country";
        var region1Id = "67019a3e-3ca4-431a-b211-b1e079fcfc3f";
        var regionName = "test-region";
        var city1Id = "f22ca69a-2040-43c2-9b5b-aebb2c92add2";
        var city1Name = "test-city-1";
        var city2Id = "85ab2ce8-332d-4eef-9979-f81d9ccb8359";
        var city2Name = "test-city-2";
        var city3Id = "e1d0c0f5-e458-42bd-98d8-0e7e4767551e";
        var city3Name = "test-city-3";

        // TODO: add another city with different region?
        // TODO: add another city with no rising trend?

        when(europeanCitiesDictionaryClient.getCityById(city1Id))
                .thenReturn(City.builder()
                        .country(countryName)
                        .city(city1Name)
                        .regionId(region1Id)
                        .region(regionName)
                        .build());
        when(europeanCitiesDictionaryClient.getCityById(city2Id))
                .thenReturn(City.builder()
                        .country(countryName)
                        .city(city2Name)
                        .regionId(region1Id)
                        .region(regionName)
                        .build());
        when(europeanCitiesDictionaryClient.getCityById(city3Id))
                .thenReturn(City.builder()
                        .country(countryName)
                        .city(city3Name)
                        .regionId(region1Id)
                        .region(regionName)
                        .build());

        // city 1 - co wzrasta przez ostatnie 3 miesiące
        // pierwszy dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.1")
                .timestamp(ZonedDateTime.now().minusMonths(3).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.2")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.3")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.4")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.5")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0")
                .co("0.6")
                .timestamp(ZonedDateTime.now().withDayOfMonth(1).minusDays(1)));

        // city 2 - pm10 wzrasta przez ostatnie 3 miesiace
        // pierwszy dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("0.25")
                .co("0")
                .timestamp(ZonedDateTime.now().minusMonths(3).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("0.50")
                .co("0")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("0.75")
                .co("0")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("1.0")
                .co("0")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("1.25")
                .co("0")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("1.50")
                .co("0")
                .timestamp(ZonedDateTime.now().withDayOfMonth(1).minusDays(1)));

        // city 3 - co i pm10 wzrasta przez ostatnie 3 miesiace
        // pierwszy dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.1")
                .co("0.2")
                .timestamp(ZonedDateTime.now().minusMonths(3).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 3
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.2")
                .co("0.4")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.3")
                .co("0.6")
                .timestamp(ZonedDateTime.now().minusMonths(2).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 2
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.4")
                .co("0.8")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1).minusDays(1)));
        // pierwszy dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.5")
                .co("1.0")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city3Id)
                .pm10("0.6")
                .co("1.2")
                .timestamp(ZonedDateTime.now().withDayOfMonth(1).minusDays(1)));

        var stats = get3MStatsByRegion(region1Id);

        assertThat(stats.risingCO3MCities()).containsExactlyInAnyOrder(city1Name, city3Name);
        assertThat(stats.risingPM103MCities()).containsExactlyInAnyOrder(city2Name, city3Name);
    }

    @Test
    void shouldGenerateEmptyReportsWhenThereIsNoData() {
        var now = ZonedDateTime.now();
        var year = now.getYear();
        var month = now.getMonthValue();
        var outputDir = reportProperties.outputDir();
        var expectedWorstCitiesPm10Path = outputDir.resolve("WORST_CITIES_PM10_%d%02d.csv".formatted(year, month));
        var expectedWorstCountriesCoPath = outputDir.resolve("WORST_COUNTRIES_CO_%d%02d.csv".formatted(year, month));
        var expectedWorstCitiesNo2Y2yPath = outputDir.resolve("WORST_CITIES_NO2_Y2Y_%d%02d.csv".formatted(year, month));

        reportGenerator.generateReports();

        assertThat(expectedWorstCitiesPm10Path).hasContent("CITY,REGION,PM10\r\n");
        assertThat(expectedWorstCountriesCoPath).hasContent("COUNTRY,CITIES_COUNT\r\n");
        assertThat(expectedWorstCitiesNo2Y2yPath).hasContent("CITY,COUNTRY,AVG_NO2_CURRENT,AVG_NO2_YEAR_BEFORE\r\n");
    }

    @Test
    void shouldGenerateWorstCitiesPm10Report() {
        var yearMonthPath = DateTimeFormatter.ofPattern("yyyyMM").format(YearMonth.now());
        var expectedWorstCitiesPm10Path = reportProperties.outputDir().resolve("WORST_CITIES_PM10_" + yearMonthPath + ".csv");

        // TODO: add test cases for other reports

        // TODO: add a test that has more than 100 cities
        //       read test data from a file

        var countryName = "test-country";
        var region1Id = "67019a3e-3ca4-431a-b211-b1e079fcfc3f";
        var regionName = "test-region";
        var city1Id = "f22ca69a-2040-43c2-9b5b-aebb2c92add2";
        var city1Name = "test-city-1";
        var city2Id = "85ab2ce8-332d-4eef-9979-f81d9ccb8359";
        var city2Name = "test-city-2";

        when(europeanCitiesDictionaryClient.getCityById(city1Id))
                .thenReturn(City.builder()
                        .country(countryName)
                        .city(city1Name)
                        .regionId(region1Id)
                        .region(regionName)
                        .build());
        when(europeanCitiesDictionaryClient.getCityById(city2Id))
                .thenReturn(City.builder()
                        .country(countryName)
                        .city(city2Name)
                        .regionId(region1Id)
                        .region(regionName)
                        .build());

        // pierwszy dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0.1")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city1Id)
                .pm10("0.15")
                .timestamp(ZonedDateTime.now().withDayOfMonth(1).minusDays(1)));

        // pierwszy dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("0.2")
                .timestamp(ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)));
        // ostatni dzien miesiąca - 1
        postMeasure(measureBuilder()
                .cityId(city2Id)
                .pm10("0.25")
                .timestamp(ZonedDateTime.now().withDayOfMonth(1).minusDays(1)));

        reportGenerator.generateWorstCitiesPm10Report();

        assertThat(expectedWorstCitiesPm10Path).hasContent(
                """
                        CITY,REGION,PM10\r
                        test-city-2,test-region,0.225\r
                        test-city-1,test-region,0.125\r
                        """
        );
    }

    private void postMeasure(MeasureUtil.MeasureBuilder builder) {
        postMeasure(builder.toJson());
    }

    private void postMeasure(String content) {
        var request = RequestEntity.post("/api/save-measure")
                .contentType(APPLICATION_JSON)
                .body(content);

        var response = restTemplate.postForEntity("/api/save-measure", request, Void.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    private Stats1HByCityResponse get1HStatsByCity(String cityId) {
        var response = restTemplate.getForEntity("/api/stats/1H/city/{cityId}", Stats1HByCityResponse.class, cityId);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private Stats3MByRegionResponse get3MStatsByRegion(String regionId) {
        var response = restTemplate.getForEntity("/api/stats/3M/{regionId}", Stats3MByRegionResponse.class, regionId);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
}

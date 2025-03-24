package com.ing.weather_monitor.tasks;

import com.ing.weather_monitor.dao.MeasureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportGenerator {
    private final MeasureRepository measureRepository;
    private final ReportProperties reportProperties;

    @Scheduled(cron = "0 0 0 1 * ?")
    public synchronized void generateReports() {
        log.info("Generating reports");

        generateWorstCitiesPm10Report();
        generateWorstCountriesCoReport();
        generateWorstCitiesNo2Y2YReport();
    }

    public void generateWorstCitiesPm10Report() {
        var outputFilePath = getWorstCitiesPm10Path();
        try (
                var bufferedWriter = Files.newBufferedWriter(outputFilePath);
                var csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)
        ) {
            csvPrinter.printRecord("CITY", "REGION", "PM10");

            var worstCitiesPm10 = measureRepository.findWorstCitiesPm10();
            for (var record : worstCitiesPm10) {
                var city = record.cityName();
                var region = record.regionName();
                var pm10 = record.avgPm10().stripTrailingZeros();
                csvPrinter.printRecord(city, region, pm10);
            }
            log.info("Successfully generated {}", outputFilePath);
        } catch (IOException e) {
            log.error("Failed to generate {}", outputFilePath, e);
        }
    }

    public void generateWorstCountriesCoReport() {
        var outputFilePath = getWorstCountriesCoPath();
        try (
                var bufferedWriter = Files.newBufferedWriter(outputFilePath);
                var csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)
        ) {
            csvPrinter.printRecord("COUNTRY", "CITIES_COUNT");

            var worstCountriesCo = measureRepository.findWorstCountriesCo();
            for (var record : worstCountriesCo) {
                var country = record.countryName();
                var citiesCount = record.cityCount();
                csvPrinter.printRecord(country, citiesCount);
            }

            log.info("Successfully generated {}", outputFilePath);
        } catch (IOException e) {
            log.error("Failed to generate {}", outputFilePath, e);
        }
    }

    public void generateWorstCitiesNo2Y2YReport() {
        var outputFilePath = getWorstCitiesNo2Y2YPath();
        try (
                var bufferedWriter = Files.newBufferedWriter(outputFilePath);
                var csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)
        ) {
            csvPrinter.printRecord("CITY", "COUNTRY", "AVG_NO2_CURRENT", "AVG_NO2_YEAR_BEFORE");

            var worstCitiesPm10 = measureRepository.findWorstCitiesNo2Y2();
            for (var record : worstCitiesPm10) {
                var city = record.cityName();
                var country = record.countryName();
                var avgNo2Current = record.avgNo2Current().stripTrailingZeros();
                var avgNo2YearBefore = record.avgNo2YearBefore().stripTrailingZeros();
                csvPrinter.printRecord(city, country, avgNo2Current, avgNo2YearBefore);
            }
            log.info("Successfully generated {}", outputFilePath);
        } catch (IOException e) {
            log.error("Failed to generate {}", outputFilePath, e);
        }
    }

    private Path getWorstCitiesPm10Path() {
        var fileName = "WORST_CITIES_PM10_%s.csv".formatted(getDateSuffix());
        return reportProperties.outputDir().resolve(fileName);
    }

    private Path getWorstCountriesCoPath() {
        var fileName = "WORST_COUNTRIES_CO_%s.csv".formatted(getDateSuffix());
        return reportProperties.outputDir().resolve(fileName);
    }

    private Path getWorstCitiesNo2Y2YPath() {
        var fileName = "WORST_CITIES_NO2_Y2Y_%s.csv".formatted(getDateSuffix());
        return reportProperties.outputDir().resolve(fileName);
    }

    private String getDateSuffix() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }
}

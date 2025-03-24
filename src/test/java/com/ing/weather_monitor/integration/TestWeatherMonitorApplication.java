package com.ing.weather_monitor.integration;

import com.ing.weather_monitor.WeatherMonitorApplication;
import org.springframework.boot.SpringApplication;

public class TestWeatherMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.from(WeatherMonitorApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}

package com.ing.weather_monitor.tasks;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties("app.report")
public record ReportProperties(
        Path outputDir
) {}

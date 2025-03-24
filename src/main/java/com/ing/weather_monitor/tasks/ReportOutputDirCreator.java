package com.ing.weather_monitor.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Component
@RequiredArgsConstructor
class ReportOutputDirCreator implements ApplicationRunner {
    private final ReportProperties reportProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Files.createDirectories(reportProperties.outputDir());
    }
}

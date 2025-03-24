package com.ing.weather_monitor.api.stats;

import com.ing.weather_monitor.services.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
class StatsController {
    private final StatsService statsService;

    @GetMapping("/3M/{regionId}")
    Stats3MByRegionResponse getStats3MByRegion(@PathVariable("regionId") String regionId) {
        return statsService.getStats3MByRegion(regionId);
    }

    @GetMapping("/1H/city/{cityId}")
    Stats1HByCityResponse getStats1HByCity(@PathVariable("cityId") String cityId) {
        return statsService.getStats1HByCity(cityId);
    }
}

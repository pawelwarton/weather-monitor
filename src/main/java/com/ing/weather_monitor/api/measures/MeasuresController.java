package com.ing.weather_monitor.api.measures;

import com.ing.weather_monitor.services.MeasuresService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class MeasuresController {
    private final MeasuresService measuresService;

    @PostMapping("/api/save-measure")
    void saveMeasure(@RequestBody Measure measure) {
        measuresService.saveMeasure(measure);
    }
}

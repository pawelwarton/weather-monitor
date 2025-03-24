package com.ing.weather_monitor.api.measures;

import com.ing.weather_monitor.services.MeasuresService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(MeasuresController.class)
class MeasuresControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    MeasuresService measuresService;

    @Captor
    ArgumentCaptor<Measure> measureArgumentCaptor;

    @Test
    void shouldSaveMeasure() {
        var content = """
                {
                    "sensorId": "2a83e44f-55c5-480e-a5ee-2bab5c04a597",
                    "cityId": "75d6753b-5f93-4db8-a9cd-506b6115b93d",
                    "PM10": "23.1",
                    "CO": "12.4",
                    "NO2": "0.39",
                    "timestamp": 1742332375
                }
                """;
        var expectedMeasure = Measure.builder()
                .sensorId("2a83e44f-55c5-480e-a5ee-2bab5c04a597")
                .cityId("75d6753b-5f93-4db8-a9cd-506b6115b93d")
                .pm10(new BigDecimal("23.1"))
                .co(new BigDecimal("12.4"))
                .no2(new BigDecimal("0.39"))
                .timestamp(Instant.ofEpochSecond(1742332375))
                .build();

        assertThat(mvc.post().uri("/api/save-measure").contentType(APPLICATION_JSON).content(content))
                .hasStatusOk();
        verify(measuresService).saveMeasure(measureArgumentCaptor.capture());
        assertThat(measureArgumentCaptor.getValue())
                .isEqualTo(expectedMeasure);
    }
}
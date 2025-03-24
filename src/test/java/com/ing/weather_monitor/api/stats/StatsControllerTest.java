package com.ing.weather_monitor.api.stats;

import com.ing.weather_monitor.services.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    StatsService statsService;

    @Test
    void shouldReturnsStats3MResponse() {
        when(statsService.getStats3MByRegion("0184c949-a842-451a-ac13-0287bfd2b574"))
                .thenReturn(
                        Stats3MByRegionResponse.builder()
                                .risingCO3MCities(List.of("Ruda Śląska", "Gliwice"))
                                .risingPM103MCities(List.of("Katowice"))
                                .build()
                );

        assertThat(mvc.get().uri("/api/stats/3M/0184c949-a842-451a-ac13-0287bfd2b574").accept(APPLICATION_JSON))
                .hasStatusOk()
                .bodyJson()
                .isEqualTo("""
                        {
                            "risingCO3MCities": ["Ruda Śląska", "Gliwice"],
                            "risingPM103MCities": ["Katowice"]
                        }
                        """);
    }

    @Test
    void shouldReturnsStats1HResponse() {
        when(statsService.getStats1HByCity("a41f694-419c-44ad-83df-cafe7daccbaa"))
                .thenReturn(
                        Stats1HByCityResponse.builder()
                                .avgNO2LastHour("23.1")
                                .maxNO2LastHour("22.31")
                                .minNO2LastHour("21.24")
                                .avgCOLastHour("12.43")
                                .maxCOLastHour("12.46")
                                .minCOLastHour("12.39")
                                .avgPM10LastHour("0.29")
                                .maxPM10LastHour("0.3")
                                .minPM10LastHour("0.27")
                                .build()
                );

        assertThat(mvc.get().uri("/api/stats/1H/city/a41f694-419c-44ad-83df-cafe7daccbaa").accept(APPLICATION_JSON))
                .hasStatusOk()
                .bodyJson()
                .isEqualTo("""
                        {
                            "avgNO2LastHour": "23.1",
                            "maxNO2LastHour": "22.31",
                            "minNO2LastHour": "21.24",
                            "avgCOLastHour": "12.43",
                            "maxCOLastHour": "12.46",
                            "minCOLastHour": "12.39",
                            "avgPM10LastHour": "0.29",
                            "maxPM10LastHour": "0.3",
                            "minPM10LastHour": "0.27"
                        }
                        """);
    }
}
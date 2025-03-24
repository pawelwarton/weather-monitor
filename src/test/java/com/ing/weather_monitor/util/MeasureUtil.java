package com.ing.weather_monitor.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MeasureUtil {

    public static MeasureBuilder measureBuilder() {
        return new MeasureBuilder();
    }

    public static class MeasureBuilder {
        private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        @JsonProperty("sensorId")
        private String sensorId;

        @JsonProperty("cityId")
        private String cityId;

        @JsonProperty("PM10")
        private BigDecimal pm10;

        @JsonProperty("CO")
        private BigDecimal co;

        @JsonProperty("NO2")
        private BigDecimal no2;

        @JsonProperty("timestamp")
        private Instant timestamp;

        public MeasureBuilder sensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public MeasureBuilder cityId(String cityId) {
            this.cityId = cityId;
            return this;
        }

        public MeasureBuilder pm10(String pm10) {
            this.pm10 = new BigDecimal(pm10);
            return this;
        }

        public MeasureBuilder co(String co) {
            this.co = new BigDecimal(co);
            return this;
        }

        public MeasureBuilder no2(BigDecimal no2) {
            this.no2 = no2;
            return this;
        }

        public MeasureBuilder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MeasureBuilder timestamp(ZonedDateTime zonedDateTime) {
            this.timestamp = zonedDateTime.toInstant();
            return this;
        }

        public MeasureBuilder timestamp(long seconds) {
            this.timestamp = Instant.ofEpochSecond(seconds);
            return this;
        }

        private MeasureBuilder() {
            sensorId = UUID.randomUUID().toString();
            cityId = UUID.randomUUID().toString();
            pm10 = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100), 2);
            co = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100), 2);
            no2 = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100), 2);
            timestamp = InstantUtil.random();
        }

        public String toJson() {
            try {
                return JSON_MAPPER.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize measure", e);
            }
        }
    }
}

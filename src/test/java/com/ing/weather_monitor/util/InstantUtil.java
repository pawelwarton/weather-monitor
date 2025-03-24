package com.ing.weather_monitor.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class InstantUtil {
    private static final long RAND_MIN = ZonedDateTime.now().minusYears(1).minusMonths(1).toEpochSecond();
    private static final long RAND_MAX = ZonedDateTime.now().toEpochSecond();

    public static Instant random() {
        var seconds = ThreadLocalRandom.current().nextLong(RAND_MIN, RAND_MAX);
        return Instant.ofEpochSecond(seconds);
    }
}

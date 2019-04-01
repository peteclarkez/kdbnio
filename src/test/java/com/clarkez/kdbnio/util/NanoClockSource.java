package com.clarkez.kdbnio.util;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

public class NanoClockSource {

    private static final Clock clock = Clock.systemUTC();

    public static Timestamp getTime() {
        final Instant instant = Instant.now(clock);
        return Timestamp.from(instant);
    }
}

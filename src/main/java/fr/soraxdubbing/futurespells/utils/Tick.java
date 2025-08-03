package fr.soraxdubbing.futurespells.utils;

import java.time.Duration;

public class Tick {
    public static Duration TICK_DURATION = Duration.ofMillis(50);

    public static int fromDuration(Duration duration) {
        return (int) (duration.toMillis() / TICK_DURATION.toMillis());
    }

    public static Duration toDuration(int ticks) {
        return Duration.ofMillis(ticks * TICK_DURATION.toMillis());
    }
}

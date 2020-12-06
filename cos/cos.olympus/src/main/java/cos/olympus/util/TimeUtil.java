package cos.olympus.util;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;

public class TimeUtil {
    private final static int TICKS_PER_SECOND = 10;

    public static int toTickSpeed(int v) {
        return v / TICKS_PER_SECOND;
    }

    public static int toTicks(int sec) {
        return sec * TICKS_PER_SECOND;
    }

    public static void sleepUntil() {
        long nowMs = currentTimeMillis();
        long waitUntil = nanoTime() + (100 - nowMs % 100) * 1_000_000 - 10_000;
        while (waitUntil > nanoTime()) {
            Thread.onSpinWait();
        }
    }

    public static void sleep(long nanos) {
        long waitUntil = nanoTime() + nanos - 50_000;
        while (waitUntil > nanoTime()) {
            Thread.onSpinWait();
        }
    }

    public static void sleepUntil(final long tickMs) throws InterruptedException {
        long nowMs = currentTimeMillis();
        long waitUntil = nanoTime() + (tickMs - nowMs % tickMs) * 1_000_000 - 50_000;
        while (waitUntil > nanoTime()) {
            Thread.sleep(1);
        }
    }
}


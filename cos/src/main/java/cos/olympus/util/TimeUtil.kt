package cos.olympus.util

object TimeUtil {
    private const val TICKS_PER_SECOND = 10

    fun toTickSpeed(v: Int): Int {
        return v / TICKS_PER_SECOND
    }

    fun toTicks(sec: Int): Int {
        return sec * TICKS_PER_SECOND
    }

    fun sleepUntil() {
        val nowMs = System.currentTimeMillis()
        val waitUntil = System.nanoTime() + (100 - nowMs % 100) * 1000000 - 10000
        while (waitUntil > System.nanoTime()) {
            Thread.onSpinWait()
        }
    }

    fun sleep(nanos: Long) {
        val waitUntil = System.nanoTime() + nanos - 50000
        while (waitUntil > System.nanoTime()) {
            Thread.onSpinWait()
        }
    }

    @Throws(InterruptedException::class)
    fun sleepUntil(tickMs: Long) {
        val nowMs = System.currentTimeMillis()
        val waitUntil = System.nanoTime() + (tickMs - nowMs % tickMs) * 1000000 - 50000
        while (waitUntil > System.nanoTime()) {
            Thread.sleep(1)
        }
    }
}


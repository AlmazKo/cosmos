package cos.olympus

import java.util.concurrent.ThreadLocalRandom

object Util {
    fun rand(origin: Int, bound: Int): Int {
        return ThreadLocalRandom.current().nextInt(origin, bound)
    }
}


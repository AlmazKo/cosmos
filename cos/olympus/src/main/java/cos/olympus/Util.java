package cos.olympus;

import java.util.concurrent.ThreadLocalRandom;

public interface Util {

    static long tsm() {
        return System.currentTimeMillis();
    }

    static int rand(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}

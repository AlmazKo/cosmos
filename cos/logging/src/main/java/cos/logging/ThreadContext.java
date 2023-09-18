package cos.logging;

import org.jetbrains.annotations.Nullable;

public final class ThreadContext {

    public static final String TRACKING_ID = "trackingId";
    public static final String TAG = "tag";
    public static final String SUB_TYPE = "subType";
    public static final String EXEC_TIME = "execTime";

    //todo add optimized small map
    private static final ThreadLocal<Dic> threadLocal = ThreadLocal.withInitial(Dic::new);

    public static @Nullable Dic getAll() {
        return threadLocal.get();
    }

    public static @Nullable String get(String key) {
        return threadLocal.get().get(key);
    }

    public static int getNullableInt(String key) {
        var value = threadLocal.get().get(key);
        if (value == null || value.isEmpty()) return Integer.MIN_VALUE;

        return Integer.parseInt(value);
    }

    public static void set(String key, String value) {
        if (value == null) return;
        threadLocal.get().set(key, value);
    }

    public static void set(String key, int value) {
        if (value == Integer.MIN_VALUE) return;

        threadLocal.get().set(key, Integer.toString(value));
    }

    public static void set(String key, long value) {
        if (value == Long.MIN_VALUE) return;

        threadLocal.get().set(key, Long.toString(value));
    }

    public static void clearAll() {
        threadLocal.get().clear();
    }

    public static void clear(String key) {
        threadLocal.get().remove(key);
    }
}

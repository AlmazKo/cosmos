package cos.logging;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class ThreadContext {
    //todo add optimized small map
    private static final ThreadLocal<HashMap<String, String>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static @Nullable String get(String key) {
        return threadLocal.get().get(key);
    }

    public static void set(String key, String value) {
        threadLocal.get().put(key, value);
    }

    public static void clearAll() {
        threadLocal.get().clear();
    }

    public static void clear(String key) {
        threadLocal.get().remove(key);
    }
}

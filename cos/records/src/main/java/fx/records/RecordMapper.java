package fx.records;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecordMapper<T extends Record> {
    private final static MethodHandles.Lookup MHL = MethodHandles.publicLookup();
    private final static ConcurrentHashMap<Class<? extends Record>, RecordMapper> CACHE = new ConcurrentHashMap<>();

    final List<Map.Entry<String, MethodHandle>> getters;

    public RecordMapper(Class<T> klass) {
        getters = build(klass);
    }

    public static <T extends Record> RecordMapper<T> get(Class<T> klass) {
        return CACHE.computeIfAbsent(klass, RecordMapper::new);
    }

    public static <T extends Record> List<Map.Entry<String, MethodHandle>> build(Class<T> klass) {
        var components = klass.getRecordComponents();
        var entries = new Map.Entry[components.length];
        RecordComponent c;
        for (int i = 0; i < components.length; i++) {
            c = components[i];
            try {
                var type = MethodType.methodType(c.getType());
                var getter = MHL.findVirtual(klass, c.getName(), type);
                entries[i] = Map.entry(c.getName(), getter);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException("Wrong record: " + klass, e);
            }
        }

        return List.of(entries);
    }
}

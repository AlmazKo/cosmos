package fx.records;

import java.util.Collection;
import java.util.Map;

public interface JSON {

    public static String stringify(Object obj) {
        var sb = new StringBuilder();
        toJson(sb, obj);
        return sb.toString();
    }

    private static void recordToJson(StringBuilder sb, Record obj) {
        sb.append('{');
        var mapper = RecordMapper.get(obj.getClass());
        try {
            for (var entry : mapper.getters) {
                sb.append('\"');
                sb.append(entry.getKey());
                sb.append('\"');
                sb.append(':');
                var value = entry.getValue().invokeWithArguments(obj); //todo add mapping
                toJson(sb, value);
                sb.append(',');
            }
        } catch (Throwable e) {
            throw new RuntimeException("Wrong record object " + obj, e);
        }
        fixTail(sb, '}');
    }

    static void toJson(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof CharSequence) {
            sb.append('"');
            sb.append((CharSequence) value);
            sb.append('"');
        } else if (value instanceof Number) {
            sb.append(value);
        } else if (value instanceof Collection) {
            sb.append('[');
            ((Collection<?>) value).forEach(v -> {
                toJson(sb, v);
                sb.append(',');
            });
            fixTail(sb, ']');
        } else if (value instanceof Object[]) {
            sb.append('[');
            for (Object v : (Object[]) value) {
                toJson(sb, v);
                sb.append(',');
            }
            fixTail(sb, ']');
        } else if (value instanceof Map) {
            sb.append('{');
            ((Map<?, ?>) value).forEach((k, kv) -> {
                sb.append('"');
                sb.append(k.toString());
                sb.append('"');
                sb.append(':');
                toJson(sb, kv);
                sb.append(',');
            });
            fixTail(sb, '}');
        } else if (value instanceof Record) {
            recordToJson(sb, (Record) value);
        } else {
            sb.append('"');
            sb.append(value);
            sb.append('"');
        }
    }

    public static void fixTail(StringBuilder sb, char bracket) {
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setCharAt(sb.length() - 1, bracket);
        } else {
            sb.append(bracket);
        }
    }

}

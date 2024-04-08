package cos.logging;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface JsonUtil {

    @Contract("null -> null")
    public static @Nullable String sanitize(@Nullable String value) {
        return value == null ? null : value
                .replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

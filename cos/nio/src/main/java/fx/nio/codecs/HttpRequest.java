package fx.nio.codecs;

import java.net.URI;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public record HttpRequest<T>(
        String method,
        URI uri,
        Map<String, String> headers,
        @Nullable T body,
        long time
) {

    boolean isKeepAlive() {
        //TODO
        return false;//"Keep-Alive".equalsIgnoreCase(headers.get("connection"));
    }
}

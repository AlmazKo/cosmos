package fx.nio.codecs;

import org.jetbrains.annotations.NotNull;

public class HttpException extends InvalidRequestException {
    public final int code;

    public HttpException(@NotNull String message, int code) {
        super(message);
        this.code = code;
    }
}

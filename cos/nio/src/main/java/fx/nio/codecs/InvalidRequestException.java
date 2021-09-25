package fx.nio.codecs;

import org.jetbrains.annotations.NotNull;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(@NotNull String message) {
        super(message, null, false, false);
    }
}

package fx.nio.codecs;

import org.jetbrains.annotations.Nullable;

public record HttpResponse<I, O>(
        HttpRequest<I> request,
        @Nullable O result,
        @Nullable Throwable error
) {
}

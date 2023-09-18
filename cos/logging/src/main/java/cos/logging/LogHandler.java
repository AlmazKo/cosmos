package cos.logging;

import org.jetbrains.annotations.Nullable;

public interface LogHandler {

    void beforePublish(Logger.Level lvl, Object msg, @Nullable Throwable throwable);

    void afterPublish(Logger.Level lvl, Object msg, @Nullable Throwable throwable);
}

package cos.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static cos.logging.ThreadContext.SUB_TYPE;

public interface Logger {
    static Logger get(@NotNull Class<?> name) {
        return new SharedLogger(name);
    }

    static Logger get(@NotNull String name) {
        return new SharedLogger(name);
    }

    Level level();

    default boolean isLog(Level level) {
        return level.compareTo(level()) >= 0;
    }

    default boolean notLog(Level level) {
        return level.compareTo(level()) < 0;
    }

    Logger setLevel(Level level);

    static Logger thread(Class<?> name) {
        return new ThreadLogger(name);
    }

    default void info(@NotNull Object msg) {
        publish(Level.INFO, msg, null);
    }

    default void info(@NotNull Consumer<Dic> msg) {
        if (notLog(Level.INFO)) return;

        var dic = new Dic();
        msg.accept(dic);
        publish(Level.INFO, dic, null);
    }

    default void info(@NotNull Object msg, @NotNull String subType) {
        if (notLog(Level.INFO)) return;

        ThreadContext.set(SUB_TYPE, subType);
        publish(Level.INFO, msg, null);
        ThreadContext.clear(SUB_TYPE);
    }

    default void warn(@NotNull Object msg) {
        publish(Level.WARN, msg, null);
    }

    default void warn(@NotNull Object msg, @NotNull String subType) {
        if (notLog(Level.WARN)) return;

        ThreadContext.set(SUB_TYPE, subType);
        publish(Level.WARN, msg, null);
        ThreadContext.clear(SUB_TYPE);
    }

    default void warn(@NotNull Object msg, @Nullable Throwable t) {
        publish(Level.WARN, msg, t);
    }

    default void error(@NotNull Object msg, @Nullable Throwable t) {
        publish(Level.ERROR, msg, t);
    }

    default void warn(@NotNull Consumer<Dic> msg, @Nullable Throwable t) {
        if (notLog(Level.WARN)) return;

        var dic = new Dic();
        msg.accept(dic);
        publish(Level.WARN, msg, t);
    }

    default void debug(@NotNull Object msg) {
        publish(Level.DEBUG, msg, null);
    }

    default void debug(@NotNull Object msg, @NotNull String subType) {
        if (notLog(Level.DEBUG)) return;

        ThreadContext.set(SUB_TYPE, subType);
        publish(Level.DEBUG, msg, null);
        ThreadContext.clear(SUB_TYPE);
    }


    void setTag(@NotNull String tag);

    void removeTag();


    void publish(Level lvl, @NotNull Object msg, @Nullable Throwable throwable);


    enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}

package cos.logging;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Logger {
    String SUB_TYPE = "subType";

    static Logger get(Class<?> name) {
        return new SharedLogger(name);
    }

    static Logger get(String name) {
        return new SharedLogger(name);
    }

    static Logger thread(Class<?> name) {
        return new ThreadLogger(name);
    }

    Logger atErrors();

    void info(String msg);

    default void info(Object msg) {
        info(msg.toString());
    }

    default void info(String subType, Object msg) {
        ThreadContext.set(SUB_TYPE, subType);
        info(msg.toString());
        ThreadContext.clear(SUB_TYPE);
    }

    void info(Consumer<Dic> msg);

    default void info(Dic msg) {
        info(msg.toString());
    }

    void warn(String msg);

    void warn(String msg, Throwable t);

    void error(String msg, Throwable t);

    void warn(Consumer<Dic> msg, Throwable e);

    void debug(String s);

    void setTag(String tag);

    void removeTag();

    void publish(Level lvl, String msg, @Nullable Throwable throwable);

    enum Level {
        DEBUG, INFO, WARN, ERROR
    }
}

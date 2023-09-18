package cos.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static cos.logging.Logger.Level.INFO;
import static cos.logging.ThreadContext.TAG;
import static cos.logging.Util.appendFileLink;
import static cos.logging.Util.appendInt;
import static cos.logging.Util.appendString;
import static cos.logging.Util.appendThread;
import static cos.logging.Util.appendTime;
import static java.lang.System.currentTimeMillis;

public final class SharedLogger implements Logger {
    private final String name;
    private Level level = LogConfig.DEFAULT_LEVEL;
    private @NotNull String tag = "";
    private final LogHandler[] handlers = LogConfig.HANDLERS;

    public SharedLogger(Class<?> klass) {
        name = klass.getSimpleName();
    }

    public SharedLogger(String name) {
        this.name = name;
    }


    @Override
    public Level level() {
        return level;
    }

    @Override
    public Logger setLevel(Level level) {
        this.level = level;
        return this;
    }

    @Override
    public void publish(Level lvl, Object msg, @Nullable Throwable throwable) {
        if (lvl.compareTo(level) < 0) return;

        try {
            for (LogHandler h : handlers) {
                h.beforePublish(lvl, msg, throwable);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        write(lvl, throwable, msg.toString());

        try {
            for (LogHandler h : handlers) {
                h.afterPublish(lvl, msg, throwable);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void write(Level lvl, @Nullable Throwable throwable, String message) {
        //extra spaced is hardcoded, can be calculated in advance
        var buf = new byte[message.length() + 300];//todo ; now it supports only latin
        int i = append(message, buf);

        (lvl.ordinal() <= INFO.ordinal() ? System.out : System.err).write(buf, 0, i + 1);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private int append(String message, byte[] buf) {
        int i = appendTime(buf, 0, currentTimeMillis());
        if (LogConfig.APPEND_FILE) i = appendFileLink(name, buf, i);
        i = appendBuild(buf, i);
        i = appendThread(buf, i);
        i = appendTag(buf, i);
        i = appendSubType(buf, i);
        buf[i++] = ' ';
        i = appendString(message, buf, i);
        if (LogConfig.APPEND_CONTEXT) i = appendContext(buf, i);
        buf[i] = '\n';
        return i;
    }

    private int appendContext(byte[] buf, int i) {
        var dic = ThreadContext.getAll();
        if (dic == null || dic.isEmpty()) {
            return i;
        }

        buf[i++] = ' ';
        return dic.append(buf, i);
    }

    private int appendTag(byte[] buf, int i) {
        final String value = (tag.isEmpty()) ? ThreadContext.get(TAG) : tag;
        if (value != null && !value.isEmpty()) {
            buf[i++] = ' ';
            i = appendString(value, buf, i);
        }
        return i;
    }

    private int appendBuild(byte[] buf, int i) {
        if (LogConfig.BUILD_ID < 0) return i;

        buf[i++] = ' ';
        buf[i++] = '(';
        i = appendInt(LogConfig.BUILD_ID, buf, i);
        buf[i++] = ')';
        return i;
    }

    private int appendSubType(byte[] buf, int i) {
        final String value = ThreadContext.get(ThreadContext.SUB_TYPE);
        if (value != null && !value.isEmpty()) {
            buf[i++] = ' ';
            i = appendString(value, buf, i);
            buf[i++] = ' ';
            buf[i++] = '-';
        }
        return i;
    }

    @Override
    public void setTag(String tag) {
        this.tag = Objects.requireNonNullElse(tag, "");
    }

    @Override
    public void removeTag() {
    }
}

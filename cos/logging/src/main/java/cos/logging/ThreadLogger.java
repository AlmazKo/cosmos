package cos.logging;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static cos.logging.Logger.Level.INFO;
import static cos.logging.Util.appendString;
import static cos.logging.Util.appendThread;
import static cos.logging.Util.appendTime;
import static cos.logging.Util.truncate;
import static java.lang.System.currentTimeMillis;

/*
GC free logger, but not thread safe
 */
public final class ThreadLogger implements Logger {
    public static final int MAX_LEN = 800;
    private final String name;
    private final byte[] buf = new byte[MAX_LEN + 100];
    private final Dic dic = new Dic();
    private Level level = LogConfig.DEFAULT_LEVEL;
    private String tag = "";

    public ThreadLogger(Class<?> klass) {
        name = klass.getSimpleName();
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
    public void info(Consumer<Dic> msg) {
        msg.accept(dic);
        info(dic);
        dic.clear();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void publish(Level lvl, Object msg, @Nullable Throwable throwable) {
        if (lvl.compareTo(level) < 0) return;

        final String message = truncate(msg.toString(), MAX_LEN);
        int i = appendTime(buf, 0, currentTimeMillis());
        i = appendThread(buf, i);
        if (!tag.isEmpty()) {
            buf[i++] = ' ';
            i = appendString(tag, buf, i);
        }
        if (LogConfig.APPEND_FILE) i = Util.appendFileLink(name, buf, i);
        buf[i++] = ' ';
        message.getBytes(0, message.length(), buf, i);
        i += message.length();
        buf[i] = '\n';

        (lvl == INFO ? System.out : System.err).write(buf, 0, i + 1);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void removeTag() {
        this.tag = "";
    }
}

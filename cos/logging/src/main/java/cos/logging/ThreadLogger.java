package cos.logging;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static cos.logging.Logger.Level.INFO;
import static cos.logging.Logger.Level.WARN;
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
    private final boolean debug = false;
    private final String name;
    private final byte[] buf = new byte[MAX_LEN + 100];
    private final Dic dic = new Dic();
    private boolean errorsOnly = false;
    private String tag = "";

    public ThreadLogger(Class<?> klass) {
        name = klass.getSimpleName();
    }

    @Override
    public void warn(String msg) {
        publish(WARN, msg, null);
    }

    @Override
    public void warn(String msg, Throwable t) {
        publish(WARN, msg, t);
    }

    @Override
    public void error(String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public void info(Consumer<Dic> msg) {
        msg.accept(dic);
        info(dic);
        dic.clear();
    }

    @Override
    public void warn(Consumer<Dic> msg, Throwable e) {
        msg.accept(dic);
        warn(dic.toString(), e);
        dic.clear();
    }

    @Override
    public void info(Dic msg) {
        info(msg.toString());
    }

    @Override
    public void info(String msg) {
        if (!errorsOnly) publish(INFO, msg, null);
    }

    @Override
    public void publish(Level lvl, String msg, @Nullable Throwable throwable) {
        msg = truncate(msg, MAX_LEN);
        int i = appendTime(buf, currentTimeMillis());
        i = appendThread(buf, i);
        if (!tag.isEmpty()) {
            buf[i++] = ' ';
            i = appendString(tag, buf, i);
        }
        if (LogConfig.APPEND_FILE) i = Util.appendFileLink(name, buf, i);
        buf[i++] = ' ';
        msg.getBytes(0, msg.length(), buf, i);
        i += msg.length();
        buf[i] = '\n';

        (lvl == INFO ? System.out : System.err).write(buf, 0, i + 1);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    @Override
    public ThreadLogger atErrors() {
        errorsOnly = true;
        return this;
    }


    @Override
    public void debug(String s) {
        //todo nothing
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

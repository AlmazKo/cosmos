package cos.logging;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static cos.logging.Logger.Level.INFO;
import static cos.logging.Logger.Level.WARN;
import static cos.logging.Util.appendFileLink;
import static cos.logging.Util.appendString;
import static cos.logging.Util.appendThread;
import static cos.logging.Util.appendTime;
import static java.lang.System.currentTimeMillis;

public final class SharedLogger implements Logger {
    private final boolean debug = false;
    private final String name;
    private boolean errorsOnly = false;
    private String tag = "";

    public SharedLogger(Class<?> klass) {
        name = klass.getSimpleName();
    }

    public SharedLogger(String name) {
        this.name = name;
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
        var dic = new Dic();
        msg.accept(dic);
        info(dic);
        dic.clear();
    }

    public void warn(Consumer<Dic> msg, Throwable e) {
        var dic = new Dic();
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
    public SharedLogger atErrors() {
        errorsOnly = true;
        return this;
    }

    @Override
    public void debug(String s) {
        //todo nothing
    }


    @Override
    public void publish(Level lvl, String msg, @Nullable Throwable throwable) {
        //100 is hardcoded, can be calculated in advance
        final String subType = ThreadContext.get(SUB_TYPE);
        //TODO optimize it
        if (subType != null && !subType.isEmpty()) {
            msg = subType + " - " + msg;
        }

        var buf = new byte[msg.length() + 100];//todo ; now it supports only latin
        int i = appendTime(buf, currentTimeMillis());
        i = appendThread(buf, i);
        if (!tag.isEmpty()) {
            i = appendString(tag, buf, i);
        }
        if (LogConfig.APPEND_FILE) i = appendFileLink(name, buf, i);
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
    public void setTag(String tag) {
        this.tag = " " + tag;
    }

    @Override
    public void removeTag() {
        this.tag = "";
    }
}

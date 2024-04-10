
package cos.logging.slf4j;

import cos.logging.LogConfig;
import cos.logging.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

public final class SlfLogger extends LegacyAbstractLogger {
    private final Logger log;

    public SlfLogger(String name) {
        this.log = Logger.get(name);
    }

    private boolean isLevelEnabled(Logger.Level logLevel) {
        return (logLevel.intLevel >= LogConfig.DEFAULT_LEVEL.intLevel);
    }

    public boolean isTraceEnabled() {
        return isLevelEnabled(Logger.Level.TRACE);
    }

    public boolean isDebugEnabled() {
        return isLevelEnabled(Logger.Level.DEBUG);
    }

    public boolean isInfoEnabled() {
        return isLevelEnabled(Logger.Level.INFO);
    }

    public boolean isWarnEnabled() {
        return isLevelEnabled(Logger.Level.WARN);
    }

    public boolean isErrorEnabled() {
        return isLevelEnabled(Logger.Level.ERROR);
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
        var fxLvl = toFx(level);
        if (log.notLog(fxLvl)) return;

        if (arguments != null && arguments.length > 0) {
            FormattingTuple tp = MessageFormatter.arrayFormat(messagePattern, arguments);
            log.publish(fxLvl, tp.getMessage(), throwable);
        } else {
            log.publish(fxLvl, messagePattern, throwable);
        }
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null;
    }

    public static Logger.Level toFx(org.slf4j.event.Level level) {
        return switch (level) {
            case ERROR -> Logger.Level.ERROR;
            case WARN -> Logger.Level.WARN;
            case INFO -> Logger.Level.INFO;
            case DEBUG -> Logger.Level.DEBUG;
            case TRACE -> Logger.Level.TRACE;
        };
    }
}
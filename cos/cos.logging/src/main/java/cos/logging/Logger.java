package cos.logging;

import java.io.PrintStream;

//val start = System.nanoTime()

public class Logger {

    private       boolean       errorsOnly = false;
    private final boolean       debug      = true;
    private final String        name;
    private final StringBuilder sb         = new StringBuilder(255);


    public Logger(Class<?> klass) {
        name = klass.getSimpleName();
    }

    public void warn(String msg) {
        log(System.err, msg);
    }

    public void info(String msg) {
        if (!errorsOnly) log(System.out, msg);
    }

    private void log(PrintStream stream, String msg) {
        appendTime(sb);
        sb.append(' ');
        appendThread(sb);
        sb.append(' ');
        appendFileName(sb);
        sb.append(' ');
        sb.append(msg);
        stream.println(sb.toString());
        sb.setLength(0);
    }

    private void appendFileName(StringBuilder sb) {
        if (debug) {
            int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
            sb.append('(');
            sb.append(name);
            sb.append(".java:");
            sb.append(line);
            sb.append(')');
        } else {
            sb.append(name);
        }
    }

    private void appendThread(StringBuilder sb) {
        String name = Thread.currentThread().getName();
        sb.append('[');
        sb.append(name);
        sb.append(']');
    }

    private void appendTime(StringBuilder sb) {
        int msDay = (int) (System.currentTimeMillis() % 86400000);
        int h = msDay / 3_600_000;
        int mi = (msDay - h * 3_600_000) / 60_000;
        int sec = (msDay - h * 3_600_000 - mi * 60_000) / 1_000;
        int ms = (msDay - h * 3_600_000 - mi * 60_000 - sec * 1000);
        if (h < 10) sb.append('0');
        sb.append(h);
        sb.append(':');
        if (mi < 10) sb.append('0');
        sb.append(mi);
        sb.append(':');
        if (sec < 10) sb.append('0');
        sb.append(sec);
        sb.append('.');
        if (ms < 10) sb.append('0');
        if (ms < 100) sb.append('0');
        sb.append(ms);
    }

    public Logger atErrors() {
        errorsOnly = true;
        return this;
    }
}

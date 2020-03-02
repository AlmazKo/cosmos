package cos.logging;

import java.io.PrintStream;

//val start = System.nanoTime()

public class Logger {

    private       boolean       errorsOnly = false;
    private final boolean       debug      = true;
    private final Class<?>      klass;
    private final String        name;
    private final StringBuilder sb         = new StringBuilder(255);


    public Logger(Class<?> klass) {
        this.klass = klass;
        name = klass.getSimpleName();
    }

    public void warn(String msg) {
        log(System.err, msg);
    }

    public void warn(String msg, Throwable t) {
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
        if (debug) {
            appendLoc(sb);
        } else {
            sb.append(name);
        }
        sb.append(' ');
        sb.append(msg);
        stream.println(sb.toString());
        sb.setLength(0);
    }

    private void appendLoc(StringBuilder sb) {
        int line = 0;
        String ext = ".java";
        var st = Thread.currentThread().getStackTrace();
        StackTraceElement ste;
        for (int i = 1; i < st.length; i++) {
            ste = st[i];
            if (st[i].getClassName().equals(klass.getName())) {
                line = st[i].getLineNumber();
                var file = ste.getFileName();
                ext = file.substring(file.lastIndexOf('.'));
                break;
            }
        }

        appendLoc(sb, line);
    }

    private void appendLoc(StringBuilder sb, int line) {
        String ext = ".java";


        sb.append('(');
        sb.append(name);
        sb.append(ext);
        sb.append(':');
        sb.append(line);
        sb.append(')');
    }


    private void appendThread(StringBuilder sb) {
        String name = Thread.currentThread().getName();
        sb.append('[');
        sb.append(name);
        sb.append(']');
    }

    private static void appendTime(StringBuilder sb) {
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

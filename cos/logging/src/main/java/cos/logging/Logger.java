package cos.logging;

import java.io.PrintStream;

import static java.lang.System.currentTimeMillis;

public class Logger {

    private final static String  loggerClassName = Logger.class.getName();
    private              boolean errorsOnly      = false;
    private final        boolean debug           = false;
    private final        String  name;
    private final        String  className;
    private final        byte[]  buf             = new byte[256];
    private final        boolean appendFile      = false;

    public Logger(Class<?> klass) {
        name = klass.getSimpleName();
        className = klass.getName();
    }

    public void warn(String msg) {
        log(System.err, msg);
    }

    public void warn(String msg, Throwable t) {
        log(System.err, msg);
        if (t != null) t.printStackTrace(System.err);
    }

    public void info(String msg) {
        if (!errorsOnly) log(System.out, msg);
    }

    private void log(PrintStream stream, String msg) {
        int i = appendTime(buf, currentTimeMillis());
        i = appendThread(buf, i);
        if (appendFile) i = appendFileLink(i);
        buf[i++] = ' ';
        msg.getBytes(0, msg.length(), buf, i);
        i += msg.length();
        buf[i] = '\n';
        stream.write(buf, 0, i + 1);
    }

    private int appendFileLink(int i) {
        buf[i++] = ' ';
        buf[i++] = '(';
//        if (debug) {
//            appendLoc(buf, i,0);
//
//
//            buf[i++] = '(';
//            sb.append(name);
//            sb.append(ext);
//            buf[i++] = ':';
//            sb.append(line);
//            buf[i++] = ')';

//        } else {
        i = appendString(name, buf, i);
//        }

//        buf[i++] = '0';

        i = appendLoc(buf, i);
        buf[i++] = ')';
        buf[i++] = ' ';
        return i;
    }

    private int appendString(String value, byte[] buf, int idx) {
        value.getBytes(0, value.length(), buf, idx);
        return idx + name.length();
    }

    private int appendLoc(byte[] buf, int idx) {
        int line = 0;
        String ext = "java";
        var st = Thread.currentThread().getStackTrace();
        StackTraceElement ste;
        for (int i = 1; i < st.length; i++) {
            ste = st[i];
            if (!ste.getClassName().equals(loggerClassName)) {
                line = st[i].getLineNumber();
                var file = ste.getFileName();
                ext = file.substring(file.lastIndexOf('.'));
                break;
            }
        }

        int i = appendString(ext, buf, idx);
        buf[i++] = ':';

        return appendInt(line, buf, i);
    }

//    private static int appendLoc(char[] buf, int i, int line) {
//        String ext = ".java";
//
//
//        buf[i++] = '(';
//        sb.append(name);
//        sb.append(ext);
//        buf[i++] = ':';
//        sb.append(line);
//        buf[i++] = ')';
//    }


    private static int appendThread(byte[] buf, int i) {
        buf[i++] = ' ';
        String name = Thread.currentThread().getName();
        buf[i++] = '[';
        name.getBytes(0, name.length(), buf, i);
        i += name.length();
        buf[i++] = ']';
        return i;
    }

    private static int appendTime(byte[] buf, long ts) {
        int msDay = (int) (ts % 86400000);
        int h = msDay / 3_600_000;
        int mi = (msDay - h * 3_600_000) / 60_000;
        int sec = (msDay - h * 3_600_000 - mi * 60_000) / 1_000;
        int ms = (msDay - h * 3_600_000 - mi * 60_000 - sec * 1000);

        int i = 0;
        buf[i++] = (byte) (h / 10 + '0');
        buf[i++] = (byte) (h % 10 + '0');
        buf[i++] = ':';
        buf[i++] = (byte) (mi / 10 + '0');
        buf[i++] = (byte) (mi % 10 + '0');
        buf[i++] = ':';
        buf[i++] = (byte) (sec / 10 + '0');
        buf[i++] = (byte) (sec % 10 + '0');
        buf[i++] = '.';
        buf[i++] = (byte) (ms / 100 + '0');
        buf[i++] = (byte) (ms % 100 / 10 + '0');
        buf[i++] = (byte) (ms % 10 + '0');
        return i;
    }

    public Logger atErrors() {
        errorsOnly = true;
        return this;
    }


    private int appendInt(int value, byte[] buf, int idx) {
        var ss = stringSize(value);
        var i = idx + ss;
        while (value > 0) {
            buf[--i] = (byte) ('0' + value % 10);
            value /= 10;
        }

        return idx + ss;
    }

    static int stringSize(int x) {
        int d = 0;
        x = -x;
        int p = -10;
        for (int i = 1; i < 10; i++) {
            if (x > p)
                return i + d;
            p = 10 * p;
        }
        return 10 + d;
    }

    public void debug(String s) {
        //todo nothing
    }
}

package cos.logging;

import java.io.PrintStream;

import static java.lang.System.currentTimeMillis;

public class Logger {

    private       boolean errorsOnly = false;
    private final boolean debug      = true;
    private final String  name;
    private final String  className;
    private final char[]  buf        = new char[256];
    private final boolean appendFile = false;

    public Logger(Class<?> klass) {
        name = klass.getSimpleName();
        className = klass.getName();
    }

    public void warn(String msg) {
        log(System.err, msg);
    }

    public void warn(String msg, Throwable t) {
        log(System.err, msg);
        t.printStackTrace();
    }

    public void info(String msg) {
        if (!errorsOnly) log(System.out, msg);
    }

    private void log(PrintStream stream, String msg) {
        int i = appendTime(buf, currentTimeMillis());
        i = appendThread(buf, i);
        if (appendFile) i = appendFileLink(i);
        buf[i++] = ' ';
        msg.getChars(0, msg.length(), buf, i);
        i += msg.length();
        buf[i] = '\u0000';

        stream.println(buf);
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
        name.getChars(0, name.length(), buf, i);
        i += name.length();
//        }
        buf[i++] = '.';
        buf[i++] = 'j';
        buf[i++] = 'a';
        buf[i++] = 'v';
        buf[i++] = 'a';
        buf[i++] = ':';
        buf[i++] = '0';
        buf[i++] = ')';
        buf[i++] = ' ';
        return i;
    }

//    private int appendLoc(char[] buf, int idx) {
//        int line = 0;
//        var st = Thread.currentThread().getStackTrace();
//        StackTraceElement ste;
//        for (int i = 1; i < st.length; i++) {
//            ste = st[i];
//            if (st[i].getClassName().equals(className)) {
//                return st[i].getLineNumber();
//                var file = ste.getFileName();
//                ext = file.substring(file.lastIndexOf('.'));
//            }
//        }
//
//        return line;
//    }

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


    private static int appendThread(char[] buf, int i) {
        buf[i++] = ' ';
        String name = Thread.currentThread().getName();
        buf[i++] = '[';
        name.getChars(0, name.length(), buf, i);
        i += name.length();
        buf[i++] = ']';
        return i;
    }

    private static int appendTime(char[] buf, long ts) {
        int msDay = (int) (ts % 86400000);
        int h = msDay / 3_600_000;
        int mi = (msDay - h * 3_600_000) / 60_000;
        int sec = (msDay - h * 3_600_000 - mi * 60_000) / 1_000;
        int ms = (msDay - h * 3_600_000 - mi * 60_000 - sec * 1000);

        int i = 0;
        buf[i++] = (char) (h / 10 + 48);
        buf[i++] = (char) (h % 10 + 48);
        buf[i++] = ':';
        buf[i++] = (char) (mi / 10 + 48);
        buf[i++] = (char) (mi % 10 + 48);
        buf[i++] = ':';
        buf[i++] = (char) (sec / 10 + 48);
        buf[i++] = (char) (sec % 10 + 48);
        buf[i++] = '.';
        buf[i++] = (char) (ms / 100 + 48);
        buf[i++] = (char) (ms % 100 / 10 + 48);
        buf[i++] = (char) (ms % 10 + 48);
        return i;
    }

    public Logger atErrors() {
        errorsOnly = true;
        return this;
    }
}

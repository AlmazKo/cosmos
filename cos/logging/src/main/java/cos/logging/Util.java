package cos.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

final class Util {

    static int appendTime(byte[] buf, long ts) {
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


    static int appendThread(byte[] buf, int i) {
        buf[i++] = ' ';
        String name = Thread.currentThread().getName();
        buf[i++] = '[';
        name.getBytes(0, name.length(), buf, i);
        i += name.length();
        buf[i++] = ']';
        return i;
    }


    static int appendInt(int value, byte[] buf, int idx) {
        var ss = stringSize(value);
        var i = idx + ss;
        while (value > 0) {
            buf[--i] = (byte) ('0' + value % 10);
            value /= 10;
        }

        return idx + ss;
    }


    static int appendString(@NotNull String value, byte[] buf, int idx) {
        value.getBytes(0, value.length(), buf, idx);
        return idx + value.length();
    }

    static int appendFileLink(String name, byte[] buf, int i) {
        buf[i++] = ' ';
        buf[i++] = '(';
//        i = appendString(name, buf, i);
        i = appendLoc(buf, i);
        buf[i++] = ')';
        return i;
    }

    static int appendLoc(byte[] buf, int idx) {
        int line = -1;
        @Nullable String file = "";
        var st = Thread.currentThread().getStackTrace();
        StackTraceElement ste;

        for (int i = 1; i < st.length; i++) {
            ste = st[i];
            if (!ste.getClassName().startsWith("cos.logging")) {
                line = ste.getLineNumber();
                file = ste.getFileName();
                break;
            }
        }
        int i = appendString(file== null? "unknown" : file, buf, idx);

        if (line != -1) {
            buf[i++] = ':';
            return appendInt(line, buf, i);
        } else {
            return appendString(":0", buf, i);
        }
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

    static String truncate(String value, int max) {
        if (value.length() < max) return value;
        //fixme: use optimized getBytes
        return value.substring(0, max);
    }

    static String getStackTrace(@Nullable Throwable t) {
        if (t == null) return null;

        var sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

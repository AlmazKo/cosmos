package cos.logging;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;

final class Util {

    static int appendTime(byte[] buf, int i, long ts) {
        i = appendDate(buf, i, ts);
        buf[i++] = ' ';
        int msDay = (int) (ts % 86400000);
        int h = msDay / 3_600_000;
        int mi = (msDay - h * 3_600_000) / 60_000;
        int sec = (msDay - h * 3_600_000 - mi * 60_000) / 1_000;
        int ms = (msDay - h * 3_600_000 - mi * 60_000 - sec * 1000);

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

    private static int appendDate(byte[] buf, int i, long ts) {
        final LocalDate dt = getDate(ts);
        final int y = dt.getYear();
        final int m = dt.getMonthValue();
        final int d = dt.getDayOfMonth();

        buf[i++] = (byte) (y / 1000 + '0');
        buf[i++] = (byte) (y % 1000 / 100 + '0');
        buf[i++] = (byte) (y % 100 / 10 + '0');
        buf[i++] = (byte) (y % 10 + '0');
        buf[i++] = '-';
        buf[i++] = (byte) (m / 10 + '0');
        buf[i++] = (byte) (m % 10 + '0');
        buf[i++] = '-';
        buf[i++] = (byte) (d / 10 + '0');
        buf[i++] = (byte) (d % 10 + '0');
        return i;
    }

    static void appendDate(StringBuilder buf, LocalDate dt) {
        final int y = dt.getYear();
        final int m = dt.getMonthValue();
        final int d = dt.getDayOfMonth();

        buf.append((char) (y / 1000 + '0'));
        buf.append((char) (y % 1000 / 100 + '0'));
        buf.append((char) (y % 100 / 10 + '0'));
        buf.append((char) (y % 10 + '0'));
        buf.append('-');
        buf.append((char) (m / 10 + '0'));
        buf.append((char) (m % 10 + '0'));
        buf.append('-');
        buf.append((char) (d / 10 + '0'));
        buf.append((char) (d % 10 + '0'));
    }

    @NotNull
    static LocalDate getCurrentDate() {
        return getDate(System.currentTimeMillis());
    }

    @NotNull
    static LocalDate getDate(long ts) {
        //todo may be cache the date for gc free
        long localEpochDay = Math.floorDiv(ts / 1000, 86_400);
        return LocalDate.ofEpochDay(localEpochDay);
    }

    @SuppressWarnings("deprecation")
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


    @SuppressWarnings("deprecation")
    static int appendString(@Nullable String value, byte[] buf, int idx) {
        if (value == null) {
            value = "null";
        }
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
            if (!ste.getClassName().startsWith("cos.logging") && !ste.getClassName().endsWith("Logger")) {
                line = ste.getLineNumber();
                file = ste.getFileName();
                break;
            }
        }
        int i = appendString(file == null ? "unknown" : file, buf, idx);

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

    //copy from org.apache.commons ExceptionUtils#getStackTrace
    @Contract("null -> null")
    public static @Nullable String getStackTrace(@Nullable final Throwable t) {
        if (t == null) return null;

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.toString();
    }

    static @Nullable String readResource(String file) {
        try {
            var raw = Util.class.getResourceAsStream(file).readAllBytes();
            return new String(raw);
        } catch (Exception e) {
            return null;
        }
    }
}

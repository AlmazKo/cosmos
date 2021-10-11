package cos.ops.parser;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public final class ByteBufferUtil {

    public static void put(ByteBuffer buf, String string) {
        buf.putInt(string == null ? -1 : string.length());
        if (string != null && string.length() > 0) {
            buf.put(string.getBytes(ISO_8859_1));
        }
    }

    public static void put(ByteBuffer buf, Enum<?> value) {
        buf.put((byte) value.ordinal());
    }

    public static void put(ByteBuffer buf, boolean value) {
        buf.put((byte) (value ? 1 : 0));
    }

    public static boolean getBool(ByteBuffer buf) {
        return buf.get() != 0;
    }


    public static <T extends Enum<T>> T getEnum(ByteBuffer buf, Class<T> eClass) {
        int idx = buf.get();
        return eClass.getEnumConstants()[idx];
    }

    public static String getString(ByteBuffer buf) {
        int len = buf.getInt();
        if (len == -1) return null;
        if (len == 0) return "";

        var data = new byte[len];
        buf.get(data);
        return new String(data, ISO_8859_1);
    }

    public static void putULong(ByteBuffer buf, Long value) {
        buf.putLong(value == null ? Long.MIN_VALUE : value);
    }

    public static Long getULong(ByteBuffer buf) {
        long value = buf.getLong();
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return value;
        }

    }

    static byte[] debug(ByteBuffer buf) {
        var dump = new byte[buf.limit() - buf.position()];
        int i = 0;
        for (int b = buf.position(); b < buf.limit(); b++) {
            dump[i++] = buf.get(b);
        }
        return dump;
    }

    static byte[] debugFull(ByteBuffer buf) {
        var dump = new byte[buf.limit()];
        for (int i = 0; i < buf.limit(); i++) {
            dump[i] = buf.get(i);
        }
        return dump;
    }
}


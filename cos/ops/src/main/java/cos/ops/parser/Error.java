package cos.ops.parser;

import java.nio.ByteBuffer;

public record Error(
        int code,
        String description
) {

    public static Error VALUE = new Error(0, null);

    public void write(ByteBuffer buf) {
        buf.putInt(code);
    }

    public static Error read(ByteBuffer buf) {
        return new Error(buf.getInt(), ByteBufferUtil.getString(buf));
    }
}

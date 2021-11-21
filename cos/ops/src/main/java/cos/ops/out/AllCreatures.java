package cos.ops.out;

import cos.ops.SomeOp;

import java.nio.ByteBuffer;

import static cos.ops.parser.ByteBufferUtil.getIntArray;
import static cos.ops.parser.ByteBufferUtil.put;

public record AllCreatures(
        int width,
        int height,
        int offsetX,
        int offsetY,
        int[] creatures
) implements SomeOp {

    public void write(ByteBuffer buf) {
        buf.putInt(width);
        buf.putInt(height);
        buf.putInt(offsetX);
        buf.putInt(offsetY);
        put(buf, creatures);
    }

    public static AllCreatures read(ByteBuffer b) {
        return new AllCreatures(b.getInt(), b.getInt(), b.getInt(), b.getInt(), getIntArray(b));
    }
}

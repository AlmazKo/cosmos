package cos.ops.parser;

import java.nio.ByteBuffer;

public record Unknown() {

    public static Unknown VALUE = new Unknown();

    public void write(ByteBuffer buf) {
    }

    public static Unknown read(ByteBuffer buf) {
        return VALUE;
    }
}

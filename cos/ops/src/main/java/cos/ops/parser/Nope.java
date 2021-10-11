package cos.ops.parser;

import java.nio.ByteBuffer;

public record Nope() {

    public static Nope VALUE = new Nope();

    public void write(ByteBuffer buf) {
    }

    public static Nope read(ByteBuffer buf) {
        return VALUE;
    }
}

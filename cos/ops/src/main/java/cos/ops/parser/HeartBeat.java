package cos.ops.parser;

import java.nio.ByteBuffer;

public record HeartBeat() {

    public static HeartBeat VALUE = new HeartBeat();

    public void write(ByteBuffer buf) {
    }

    public static HeartBeat read(ByteBuffer buf) {
        return VALUE;
    }
}

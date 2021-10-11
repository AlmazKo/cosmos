package fx.nio.codecs;

import java.nio.ByteBuffer;

public interface BufferReadable {
    void read(ByteBuffer buf);
}

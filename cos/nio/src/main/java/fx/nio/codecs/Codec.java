package fx.nio.codecs;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;

public interface Codec<I, O> extends CodecOutput<O>, AutoCloseable {

    void read(ByteBuffer buf);

    void write(O object);

    void handler(@NotNull CodecInput<I> consumer);

    void close();
}

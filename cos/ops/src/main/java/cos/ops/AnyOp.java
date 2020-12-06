package cos.ops;

import java.nio.ByteBuffer;

public interface AnyOp {
    int id();

    int userId();

    byte code();

    void write(ByteBuffer buf);
}

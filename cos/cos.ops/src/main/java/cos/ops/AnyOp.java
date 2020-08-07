package cos.ops;

import java.nio.ByteBuffer;

import java.nio.ByteBuffer;

public interface AnyOp {
    int id();
    int userId();
    byte code();
//    val code: Byte
//    val userId: Int

    void write(ByteBuffer buf);
}

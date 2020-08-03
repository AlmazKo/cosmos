package cos.ops;

import java.nio.ByteBuffer;

public interface OutOp extends AnyOp {
//    int id();
//    val code: Byte
//    val userId: Int


    void write(ByteBuffer buf);
}

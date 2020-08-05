package cos.ops;

import java.nio.ByteBuffer;

public interface OutOp extends AnyOp {
    //    int id();
    int tick();


    void write(ByteBuffer buf);
}

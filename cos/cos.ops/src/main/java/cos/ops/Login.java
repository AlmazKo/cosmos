package cos.ops;

import java.nio.ByteBuffer;

public record Login(
        @Override byte code,
        @Override int id,
        int userId
) implements AnyOp {

    public Login(int id, int userId) {
        this(Op.LOGIN, id, userId);
    }

    public static Login create(ByteBuffer b) {
        return new Login(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
//    var code: Byte = Op.LOGIN
}
//) : AnyOp {
//
//
//    constructor(b: ByteBuffer) : this(
//        id = b.int,
//        userId = b.int
//    )
//
//}

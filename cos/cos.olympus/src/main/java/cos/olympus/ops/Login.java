package cos.olympus.ops;

import java.nio.ByteBuffer;

public record Login(@Override int id, int userId) implements AnyOp {

    public static Login create(ByteBuffer b) {
        return new Login(b.getInt(), b.getInt());
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

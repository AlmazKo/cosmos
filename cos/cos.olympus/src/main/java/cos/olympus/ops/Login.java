package cos.olympus.ops;

public class Login implements AnyOp {
    public final int id;
    public final int userId;

    public Login(int id, int userId) {
        this.id = id;
        this.userId = userId;
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

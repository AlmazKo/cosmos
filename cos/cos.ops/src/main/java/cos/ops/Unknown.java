package cos.ops;

import java.nio.ByteBuffer;

public record Unknown(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements AnyOp {

    public Unknown(int id, int userId) {
        this(Op.LOGIN, id, userId);
    }

    public static Unknown read(ByteBuffer b, byte len) {
        b.position(b.position() + len);
        return new Unknown(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}

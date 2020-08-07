package cos.ops;

import java.nio.ByteBuffer;

public record Login(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements AnyOp {

    public Login(int id, int userId) {
        this(Op.LOGIN, id, userId);
    }

    public static Login read(ByteBuffer b) {
        return new Login(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}

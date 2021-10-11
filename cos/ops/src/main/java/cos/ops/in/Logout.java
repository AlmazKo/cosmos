package cos.ops.in;

import cos.ops.InOp;
import cos.ops.Op;

import java.nio.ByteBuffer;

public record Logout(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements InOp {

    public Logout(int id, int userId) {
        this(Op.LOGOUT, id, userId);
    }

    public static Logout read(ByteBuffer b) {
        return new Logout(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}

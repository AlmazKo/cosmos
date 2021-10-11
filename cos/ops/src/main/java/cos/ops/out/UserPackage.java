package cos.ops.out;

import java.nio.ByteBuffer;

import static cos.ops.Registry.PARSER;

public record UserPackage(
        int tick,
        int userId,
        Record[] ops
) {

    public void write(ByteBuffer buf) {
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(ops.length);

        for (Record op : ops) {
            PARSER.writeOnlyData(buf, op);
        }
    }

    public static UserPackage read(ByteBuffer buf) {
        int tick = buf.getInt();
        int userId = buf.getInt();
        int len = buf.getInt();
        if (len == 0) return new UserPackage(tick, userId, new Record[0]);

        var ops = new Record[len];

        for (int i = 0; i < len; i++) {
            ops[i] = PARSER.readOnlyData(buf);
        }

        return new UserPackage(tick, userId, ops);
    }
}

package cos.ops.parser;

import java.nio.ByteBuffer;

public class ByteReader {

    public static final int BYTES_CMD = 1;
    public static final int BYTES_INF = 1;
    public static final int BYTES_LEN = 4;
    public static final int BYTES_SEQ = 4;
    public static final int HEADER = BYTES_CMD + BYTES_INF + BYTES_SEQ + BYTES_LEN;

    private final OpParser parser;
    private final OpConsumer ins;

    public ByteReader(OpParser parser, OpConsumer out) {
        this.parser = parser;
        this.ins = out;
    }

    public void read(final ByteBuffer in) {
        in.flip();

        while (enoughData(in)) {
            if (in.get(in.position()) == 0) {//TODO: opcode is zero?
                System.out.println("--- no data");
                break;
            }

            final byte code = in.get();
            final OpType type = OpType.values()[in.get()];
            final int seqId = in.getInt();
            final int len = in.getInt();
            final int pos = in.position();
            final Object data;
            if (type == OpType.RESPONSE_MANY || type == OpType.EVENT_MANY) {
                data = parser.readMany(in, code);
            } else {
                data = parser.read(in, code);
            }

            in.position(pos + len);
            ins.accept(data, seqId, type);
        }

        if (in.position() > 0) {
            in.compact();
        } else {
            in.position(in.limit());
            throw new RuntimeException("Too much message, max size: " + in.capacity() + " bytes");
        }
    }

    private boolean enoughData(ByteBuffer in) {
        return in.remaining() >= HEADER &&
               (in.remaining() - HEADER) >= in.getInt(in.position() + BYTES_CMD + BYTES_INF + BYTES_LEN);
    }
}

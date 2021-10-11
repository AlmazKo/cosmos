package cos.ops.parser;

import cos.ops.Registry;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static cos.ops.parser.ByteReader.BYTES_LEN;
import static cos.ops.parser.OpType.REQUEST;

public final class ByteWriter {

    private final OpParser      parser;
    public final  ByteBuffer    buf;
    public final  AtomicInteger seq = new AtomicInteger(0);

    public ByteWriter(OpParser parser, ByteBuffer buffer) {
        this.parser = parser;
        this.buf = buffer;
    }

    public void write(Record op) {
        write(op, seq.incrementAndGet(), REQUEST);
    }

    public void write(Record[] ops, int seqId, OpType type) {
        //todo check buffer limits

        if (ops.length == 0) {
            buf.put(Registry.NOPE_OP);
            buf.put((byte) type.ordinal());
            buf.putInt(seqId);
            return;
        }

        buf.put(parser.toOpCode(ops[0]));
        buf.put((byte) type.ordinal());
        buf.putInt(seqId);

        final int pos = buf.position();
        buf.position(pos + BYTES_LEN);
        parser.write(buf, ops);
        final int opLength = (buf.position() - pos - BYTES_LEN);
        buf.putInt(pos, opLength);//writing the length
    }

    public void write(Record op, int seqId, OpType type) {
        //todo check buffer limits
        int start = buf.position();

        buf.put(parser.toOpCode(op));
        buf.put((byte) type.ordinal());
        buf.putInt(seqId);
        final int pos = buf.position();
        buf.position(pos + BYTES_LEN);

        parser.write(buf, op);

        final int opLength = (buf.position() - pos - BYTES_LEN);
        buf.putInt(pos, opLength);//writing the length
        //System.out.println("Written "+ op.getClass().getSimpleName() +" with "+ (buf.position()-start) + " bytes");
    }

    public void writeIn(Record op) {
        //todo check buffer limits
        buf.put(parser.toOpCode(op));
        final int pos = buf.position();
        buf.position(pos + BYTES_LEN);
        parser.write(buf, op);
        final int opLength = (buf.position() - pos - BYTES_LEN);
        buf.putInt(pos, opLength);//writing the length
    }
}

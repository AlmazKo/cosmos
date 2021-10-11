package cos.olympus.game.api;

import cos.logging.Logger;
import cos.ops.AnyOp;
import cos.ops.parser.ByteReader;
import cos.ops.parser.ByteWriter;
import cos.ops.parser.OpType;
import fx.nio.RawChannel;
import fx.nio.codecs.BufferReadable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static cos.ops.Registry.PARSER;

public class Connection implements BufferReadable {
    public final static AtomicInteger seq = new AtomicInteger(0);

    private final Logger logger = Logger.get(Connection.class);

    private final RawChannel ch;
    private final ByteReader reader;
    private final ByteWriter writer;
    private ArrayList<AnyOp> ins = new ArrayList<>();

    public Connection(RawChannel ch) {
        this.ch = ch;
        this.writer = new ByteWriter(PARSER, ch.out());
        this.reader = new ByteReader(PARSER, (Object op, int seqId, OpType type) -> {
            logger.info("New op " + op);
            ins.add((AnyOp) op);
        });
    }

    public final void collect(Collection<AnyOp> collector) {
        if (!ins.isEmpty()) {
            collector.addAll(ins);
            ins = new ArrayList<>(ins.size());
        }
    }

    @Override
    public void read(ByteBuffer in) {
        reader.read(in);
    }

    public void flush() {
        try {
            ch.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        return ins.isEmpty() && !ch.isOpen();
    }

    public void write(Record op) {
        writer.write(op, seq.incrementAndGet(), OpType.EVENT);
    }
}

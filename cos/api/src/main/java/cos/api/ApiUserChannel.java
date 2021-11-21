package cos.api;

import cos.logging.Logger;
import cos.ops.Registry;
import cos.ops.out.UserPackage;
import cos.ops.parser.ByteReader;
import cos.ops.parser.ByteWriter;
import cos.ops.parser.OpType;
import fx.nio.ReadChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class ApiUserChannel implements ReadChannel, AutoCloseable {
    private final SocketChannel ch;
    private Logger logger = Logger.get(getClass());
    private AtomicInteger SEQ = new AtomicInteger(0);
    private Consumer<Record> consumer;
    private ByteBuffer input = ByteBuffer.allocate(16 * 1024);
    private ByteBuffer output = ByteBuffer.allocate(16 * 1024);
    private ByteWriter writer = new ByteWriter(Registry.PARSER, output);
    private ByteReader reader = new ByteReader(Registry.PARSER, this::onData);

    ApiUserChannel(SocketChannel ch) {
        this.ch = ch;
    }

    void start(Consumer<Record> consumer) {
        this.consumer = consumer;
    }

    private void onData(Object op, int seqId, OpType t) {
        if (op instanceof Object[]) {
            logger.info("<< #$seqId $t ${op.size} ops");
        } else if (op instanceof UserPackage) {
//            if (op.userId() < 1000)
//                logger.info("<< #$seqId $t UserPackage[tick=${op.tick()},user=${op.userId()}, ops=${op.ops().size}]")
            consumer.accept((UserPackage) op);
        } else {
//            logger.info("<< #$seqId $t $op");
            consumer.accept((Record) op);
        }
    }

    @Override public void read() {
        int read = 0;

        try {
            read = ch.read(input);
        } catch (IOException e) {
            logger.error("Failed to read", e);
            close();
            return;
        }
        if (read == -1) {
            close();
            return;
        }

        reader.read(input);
        input.limit(input.capacity());
    }

    int write(Record op, OpType type) {
        var seqId = SEQ.incrementAndGet();
        logger.info(">> #" + seqId + ' ' + op);
        writer.write(op, seqId, type);

        try {
            moveToChannel();
        } catch (IOException e) {
            logger.error("Failed to write: $op", e);
        }

        return seqId;
    }

    void moveToChannel() throws IOException {
        if (!ch.isOpen()) return;

        if (output.position() > 0) {
            output.flip();
            var size = output.remaining();
            var written = ch.write(output);
            //todo move from sync
            if (written < size) {
                // log.warn("Full size wasn't written")
            }
            output.clear();
            //            log.info("Written: $written")
        }
    }


    @Override public void close() {
        if (ch.isOpen()) {
            try {
                ch.close();
                logger.info("Closed");
            } catch (IOException e) {
                // log.warn("Fail during closing ", e)
            }
        }
    }
}

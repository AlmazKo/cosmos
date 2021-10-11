package fx.nio;

import cos.logging.Logger;
import fx.nio.codecs.BufferReadable;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import static fx.nio.Session.INC;

public class RawChannel implements ReadChannel, WritableByteChannel {
    private final Logger logger = Logger.get(RawChannel.class);
    private final SocketChannel ch;
    private final Session session;
    private volatile BufferReadable codec;

    public RawChannel(SocketChannel ch) {
        this.ch = ch;
        try {
            this.session = new Session(INC.incrementAndGet(), ch.getRemoteAddress());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.setTag("#" + session.id);
        logger.info("new", session);
    }

    public void register(BufferReadable codec) {
        this.codec = codec;
    }

    @Override
    public void read() {
        if (codec == null) return;

        var in = session.in;
        int read = 0;
        try {
            read = ch.read(in);
        } catch (IOException e) {
            onUserDisconnected(e.toString());
            return;
        }

//        logger.info("Read " + read);
        if (read == -1) {
            onUserDisconnected("no data");
            return;
        }

        if (in.remaining() < 2 || in.remaining() < in.get(1)) {
            //not enough data
            logger.warn("not enough data " + session);
            return;
        }

        codec.read(in);
        in.clear();
    }


    void onUserDisconnected(String reason) {
        logger.info("Closed(" + reason + "):  " + session);
        this.close();
    }

    @Override
    public boolean isOpen() {
        return ch.isOpen();
    }

    public void close() {
        if (codec instanceof Closeable) {
            try {
                ((Closeable) codec).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ch.isOpen()) {
            try {
                ch.close();
                logger.info("Closed " + session);
            } catch (IOException e) {
                logger.warn("Fail during closing " + session.toShortString(), e);
            }
        }
    }


    public ByteBuffer out() {
        return session.out;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return ch.write(src);//fixme
    }

    public void flush() throws IOException {
        if (!ch.isOpen()) return;

        //logger.info("moveToChannel $ch " + session.out)
        var out = session.out;
        if (out.position() > 0) {
            out.flip();
            var size = out.remaining();
            var written = ch.write(out);
            //todo move from sync
            if (written < size) {
                logger.warn("Full size wasn't written");
            }
            out.clear();
            //fixme: to do something with over
            //logger.info("Written: " + written);
        }
    }

    public SocketAddress getRemoteAddress() {
        try {
            return ch.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

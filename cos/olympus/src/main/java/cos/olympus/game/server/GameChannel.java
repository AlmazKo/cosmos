package cos.olympus.game.server;

import cos.logging.Logger;
import cos.ops.AnyOp;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.in.FireballEmmit;
import cos.ops.in.ForcedExit;
import cos.ops.in.Login;
import cos.ops.in.MeleeAttack;
import cos.ops.in.Move;
import cos.ops.in.ShotEmmit;
import cos.ops.in.StopMove;
import cos.ops.out.Unknown;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public final class GameChannel {
    private final static Logger        logger = new Logger(GameChannel.class);
    private final static AtomicInteger inc    = new AtomicInteger();

    private final SocketChannel    ch;
    private final Session          session;
    private final ArrayList<AnyOp> ins = new ArrayList<>();

    public GameChannel(SocketChannel ch) throws IOException {
        this.ch = ch;
        this.session = new Session(inc.incrementAndGet(), ch.getRemoteAddress());
    }

    public boolean isFinished() {
        synchronized (ins) {
            return ins.isEmpty() && !ch.isOpen();
        }
    }

    private void close() {
        if (ch.isOpen()) {
            try {
                ch.socket().close();
                ch.close();
            } catch (IOException e) {
                logger.warn("Fail during closing " + session.toShortString(), e);
            }
        }
    }

    public void write(OutOp op) {
        logger.debug("Sending " + op);
        synchronized (session.out) {
            write(session.out, op);
        }

        if (op.code() == Op.DISCONNECT) {
            session.closing = true;
        }
    }

    private static void write(ByteBuffer bb, OutOp op) {
        //todo check buffer limits
        bb.put(op.code());
        int pos = bb.position();
        bb.position(pos + 1);
        op.write(bb);
        byte opLength = (byte) (bb.position() - pos - 1);
        bb.put(pos, opLength);//write the length
    }

    public void flush() {
        try {
            moveToChannel();
        } catch (IOException e) {
            logger.warn("Fail during writing " + session, e);
        }
    }

    //call from Game
    public final void collect(Collection<AnyOp> collector) {
        synchronized (ins) {
            if (!ins.isEmpty()) {
                collector.addAll(ins);
                ins.clear();
            }
        }
    }


    final void read() {
        var in = session.in;
        int read = 0;//todo handle  SocketException
        try {
            read = ch.read(in);
        } catch (IOException e) {
            onUserDisconnected(e.toString());
            return;
        }

        //logger.info("Read " + read);
        if (read == -1) {
            onUserDisconnected("no data");
            return;
        }

        if (in.remaining() < 2 || in.remaining() < in.get(1)) {
            //not enough data
            logger.warn("not enough data " + session);
            return;
        }

        in.flip();
        while (in.hasRemaining()) {
            var op = parseOp(in);
            if (session.userId == 0 && op instanceof Login) {
                session.userId = op.userId();
                logger.info("Connected: " + session.toShortString());
            }
            addToIns(op);
        }
        in.clear();
    }

    void onUserDisconnected(String reason) {
        addToIns(new ForcedExit(0, session.userId));//todo hardcoded id
        logger.info("Closed(" + reason + "):  " + session.toShortString());
        this.close();
    }

    private void addToIns(AnyOp op) {
        synchronized (ins) {
            if (op.code() != Op.NOPE) {
                ins.add(op);
            }
        }
    }

    //call from network
    public void moveToChannel() throws IOException {
        if (!ch.isOpen()) return;

        var out = session.out;
        synchronized (session.out) {
            if (out.position() > 0) {
                out.flip();
                int size = out.remaining();
                int written = ch.write(out);
                //todo move from sync
                if (written < size) {
                    logger.warn("Full size wasn't written");
                }
                out.clear();
                logger.debug("Written: " + written);
            }
        }

        if (session.closing) {
            logger.info("Closed(by server):  " + session.toShortString());
            close();
        }
    }

    public final int userId() {
        return session.userId;
    }

    private static @NotNull AnyOp parseOp(ByteBuffer b) {
        var opCode = b.get();
        var length = b.get();
        return switch (opCode) {
            case Op.LOGIN -> Login.read(b);
            case Op.MOVE -> Move.read(b);
            case Op.STOP_MOVE -> StopMove.read(b);
            case Op.EMMIT_FIREBALL -> FireballEmmit.read(b);
            case Op.EMMIT_SHOT -> ShotEmmit.read(b);
            case Op.MELEE_ATTACK -> MeleeAttack.read(b);
            default -> Unknown.read(b, length);
        };
    }
}

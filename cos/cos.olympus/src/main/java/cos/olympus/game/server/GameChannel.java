package cos.olympus.game.server;

import cos.logging.Logger;
import cos.ops.AnyOp;
import cos.ops.FireballEmmit;
import cos.ops.Login;
import cos.ops.MeleeAttack;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.ShotEmmit;
import cos.ops.StopMove;
import cos.ops.Unknown;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class GameChannel /*implements ScatteringByteChannel, GatheringByteChannel*/ {
    private final static Logger logger = new Logger(GameChannel.class);


    private final static AtomicInteger    inc = new AtomicInteger();
    private final        SelectionKey     key;
    private final        SocketChannel    ch;
    private final        Session          session;
    private final        ArrayList<AnyOp> ins = new ArrayList<>();

    public GameChannel(SelectionKey key, SocketChannel ch) throws IOException {
        this.key = key;
        this.ch = ch;
        this.session = new Session(inc.incrementAndGet(), ch.getRemoteAddress());
    }

    public boolean isOpen() {
        return ch.isOpen();
    }

    public void close() throws IOException {
        ch.socket().close();
        ch.close();
    }

    public void write(OutOp op) {
        write(session.out, op);
    }

    public void flush() {
//
//        if(ops.isEmpty())return;
//        //write to buffer
//
//        logger.info("Writing ops to buffer ...");
//        for (OutOp op : ops) {
//            if (op.code() == Op.DISCONNECT) {
//                session.close = true;
//            }
//            write(session.out, op);
//        }

        key.interestOpsAnd(OP_WRITE);
    }

    private void write(ByteBuffer bb, OutOp op) {
        //todo check buffer limits
        bb.put(op.code());
        int pos = bb.position();
        bb.position(pos + 1);
        op.write(bb);
        byte opLength = (byte) (bb.position() - pos - 1);
        bb.put(pos, opLength);//write the length
    }



    public void collect(Collection<AnyOp> collector) {
        if (!ins.isEmpty()) {
            collector.addAll(ins);
            ins.clear();
        }
    }


    void read() throws IOException {
        var in = session.in;
        var read = ch.read(in);//todo handle  SocketException

        //ogger.info("Read " + read);
        if (read == -1) {
            // userSessions.remove(session.userId);
            //actionsBuffer.add(new Exit(0, session.userId));//todo hardcoded id
            logger.info("Closing ... " + session);
            this.close();
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
//                logger.info("Authorized: " + session);
            }

//            logger.info("Op: " + op);
            if (op.code() != Op.NOPE) {
                ins.add(op);//todo synhronized
            }
        }
        in.clear();
        // if data exists then call       in.compact();
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

    public void onWrite() {
        //move buffer
        key.interestOps(OP_READ);
    }

    public int userId() {
        return session.userId;
    }
}

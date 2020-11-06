package cos.olympus.game.server;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.Responses;
import cos.ops.AnyOp;
import cos.ops.Exit;
import cos.ops.Login;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.StopMove;
import cos.ops.Unknown;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public final class GameServer {
    private volatile     boolean                             running      = true;
    private final        AtomicInteger                       inc          = new AtomicInteger();
    private final static Logger                              logger       = new Logger(GameServer.class);
    private final        DoubleBuffer<AnyOp>                 actionsBuffer;
    private final        Responses                           responses;
    private              HashMap<Integer, @Nullable Session> userSessions = new HashMap<>();

    public GameServer(DoubleBuffer<AnyOp> actionsBuffer, Responses responses) {
        this.actionsBuffer = actionsBuffer;
        this.responses = responses;
    }

    void start(final int port) throws IOException {
        var selector = setupServerSocket(port);
        logger.info("Server started");

        while (running) {
            selector.select();
            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();

                if (!key.isValid()) {
                    logger.warn("NOT isValid");
                    //nothing
                } else if (key.isAcceptable()) {
                    accept(key, selector);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }

            prepareResponses();
        }
    }


    private void prepareResponses() {
        if (responses.ops.isEmpty()) return;

//        logger.info("Writing ops to buffer ...");
        for (OutOp op : responses.ops) {
            if (op.userId() >= 10000) continue;

            var sess = userSessions.get(op.userId());
            if (sess == null) {
                logger.info("Not exists connection for op: " + op);
            } else {
                if (op.code() == Op.DISCONNECT) {
                    sess.close = true;
                }
                write(sess.out, op);
            }
        }
        responses.ops.clear();
    }

    private void write(ByteBuffer bb, OutOp op) {
        bb.put(op.code());
        int pos = bb.position();
        bb.position(pos + 1);
        op.write(bb);
        bb.put(pos, (byte) (bb.position() - 2));//write the length
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        var ch = ((ServerSocketChannel) key.channel()).accept();
        ch.configureBlocking(false);
        var session = new Session(inc.incrementAndGet(), ch.getRemoteAddress());
        ch.register(selector, OP_READ | OP_WRITE, session);
//        logger.info("Accepted: " + session);
    }

    private void write(SelectionKey key) throws IOException {
        var session = (Session) key.attachment();
        var out = session.out;

        if (out.position() > 0) {
            //  logger.info("Writing... " + session);
            out.flip();
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write(out);
            out.clear();
        }

        if (session.close) {
            logger.info("Closing(internally) ... " + session);
            key.channel().close();
        }
    }

    private void read(SelectionKey key) throws IOException {
        var session = (Session) key.attachment();
        //logger.info("Reading... " + session);
        var ch = (SocketChannel) key.channel();
        var in = session.in;
        var read = ch.read(in);

        //ogger.info("Read " + read);
        if (read == -1) {
            userSessions.remove(session.userId);
            actionsBuffer.add(new Exit(0, session.userId));//todo hardcoded id
            logger.info("Closing ... " + session);
            ch.close();
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
                userSessions.put(op.userId(), session);
//                logger.info("Authorized: " + session);
            }

//            logger.info("Op: " + op);
            if (op.code() != Op.NOPE) {
                actionsBuffer.add(op);
            }
        }
        in.clear();
        // if data exists then call       in.compact();
    }

    private Selector setupServerSocket(int port) throws IOException {
        var selector = Selector.open();
        var server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.bind(new InetSocketAddress(port));
        server.register(selector, OP_ACCEPT);
        return selector;
    }


    private @NotNull AnyOp parseOp(ByteBuffer b) {
        var opCode = b.get();
        var length = b.get();
        return switch (opCode) {
            case Op.LOGIN -> Login.read(b);
            case Op.MOVE -> Move.read(b);
            case Op.STOP_MOVE -> StopMove.read(b);
            default -> Unknown.read(b, length);
        };
    }


    public static void run(DoubleBuffer<AnyOp> requests, Responses responses) {

        new Thread(() -> {
            try {
                new GameServer(requests, responses).start(6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "GameServer").start();


    }
}


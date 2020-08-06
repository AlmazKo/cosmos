package cos.olympus.game.server;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.Responses;
import cos.ops.AnyOp;
import cos.ops.Login;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
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

public final class GameServer {
    private volatile     boolean                             running      = true;
    private final        AtomicInteger                       inc          = new AtomicInteger();
    private final static Logger                              logger       = new Logger(GameServer.class);
    private final        DoubleBuffer<AnyOp>                 actionsBuffer;
    private final        Responses                           responses;
    private              HashMap<Integer, @Nullable Session> userSessions = new HashMap<>();
//    private              HashMap<Integer, @Nullable Session> anonSessions = new HashMap<>();

    public GameServer(DoubleBuffer<AnyOp> actionsBuffer, Responses responses) {
        this.actionsBuffer = actionsBuffer;
        this.responses = responses;
    }


    void start(int port) throws IOException, InterruptedException {
        var selector = Selector.open();
        setupSocket(port, selector);

        logger.info("I'm a server and i'm waiting for new connection and buffer select...");
        while (running) {

            selector.select(500);
            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();

                if (!key.isValid()) {
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

        logger.info("Writing ops to buffer ...");
        for (OutOp op : responses.ops) {
            var sess = userSessions.get(op.userId());
            if (sess == null) {
                logger.info("Not exists connection for op: " + op);
            } else {
                op.write(sess.out);
            }
        }
        responses.ops.clear();
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        var ch = ((ServerSocketChannel) key.channel()).accept();
        ch.configureBlocking(false);
        var clientKey = ch.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        var session = new Session(inc.incrementAndGet(), ch.getRemoteAddress());
        logger.info("Accepted: " + session);
        clientKey.attach(session);
    }

    private void write(SelectionKey key) throws IOException {
        var session = (Session) key.attachment();
//        logger.info("Writing... " + session);
        var out = session.out;
        //                    logger.info("isWritable");

        if (!out.hasRemaining()) {
            logger.info("Writing...");
            SocketChannel socketChannel = (SocketChannel) key.channel();
            session.out.flip();
            socketChannel.write(session.out);
            out.compact();
        }
    }

    private void read(SelectionKey key) throws IOException {
        var session = (Session) key.attachment();
        logger.info("Reading... " + session);
        var ch = (SocketChannel) key.channel();
        var in = session.in;
        ch.read(in);

        if (in.get(0) == Op.NOPE) {
            logger.info("No Data, close it");
            ch.close();
            key.cancel();
            return;
        }

        in.flip();
        while (in.hasRemaining()) {
            var op = parseOp(in);

            if (session.userId == 0 && op instanceof Login) {
                session.userId = op.userId();
                userSessions.put(op.userId(), session);
                logger.info("Authorized: " + session);
            }

            logger.info("Op: " + op);
            if (op != null) {
                actionsBuffer.add(op);
            } else {
                ch.close();
                key.cancel();
            }
        }
        in.clear();

//        in.clear();
        in.compact();
    }

    private void setupSocket(int port, Selector selector) throws IOException {
        var socket = ServerSocketChannel.open();
        socket.configureBlocking(false);
        //set some options
        socket.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        socket.bind(new InetSocketAddress(port));
        socket.register(selector, SelectionKey.OP_ACCEPT);
    }


    private AnyOp parseOp(ByteBuffer b) {
        switch (b.get()) {
            case Op.LOGIN -> {
                return Login.create(b);
            }
            case Op.MOVE -> {
                return Move.create(b);
            }
//            case Op.FINISH -> {return null}
            default -> {
                return null;
            }
        }
    }


    public static void run(DoubleBuffer<AnyOp> requests, Responses responses) {

        new Thread(() -> {
            try {
                new GameServer(requests, responses).start(6666);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "GameServer").start();


    }
}


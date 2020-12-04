package cos.olympus.game.server;

import cos.logging.Logger;
import cos.olympus.Responses;
import cos.olympus.util.DoubleBuffer;
import cos.ops.AnyOp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.function.BiConsumer;

import static java.lang.Boolean.TRUE;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public final class GameServer {
//    private final        BiConsumer<GameChannel, Boolean> listener;
    private final Sessions sessions;
    volatile             boolean                          running = true;
    //    private final        AtomicInteger                       inc          = new AtomicInteger();
    private final static Logger                           logger  = new Logger(GameServer.class);
//    private final        DoubleBuffer<AnyOp>                 actionsBuffer;
//    private final        Responses                           responses;
//    private              HashMap<Integer, @Nullable Session> userSessions = new HashMap<>();
//    private              HashMap<SelectionKey, GameChannel>  channels     = new HashMap<>();

    public GameServer(Sessions sessions) {
//        this.actionsBuffer = actionsBuffer;
//        this.responses = responses;
        this.sessions = sessions;
    }

    void start(final int port) throws IOException, InterruptedException {
        var selector = setupServerSocket(port);
        logger.info("Server started");

        while (selector.isOpen() && running) {
            try {
                if (selector.select(500) == 0) break;
            } catch (IOException ex) {
                ex.printStackTrace();
                // handle exception
                break;
            }

            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();
                logger.info("readable=" + key.isReadable() + " writable=" + key.isWritable());

                if (!key.isValid()) {
                    logger.warn("NOT isValid");
                    //nothing
                } else if (key.isAcceptable()) {
                    accept(key, selector);
                } else if (key.isReadable()) {
                    ((GameChannel) key.attachment()).read();
                } else if (key.isWritable()) {
                    ((GameChannel) key.attachment()).onWrite();
                    logger.info("OP_READ");
                }
            }
        }
    }

    /*

        private void prepareResponses(Selector selector) {
            if (responses.ops.isEmpty()) return;

            logger.info("Writing ops to buffer ...");
            for (OutOp op : responses.ops) {
                if (op.userId() >= 10000) continue;

                var sess = userSessions.get(op.userId());
                if (sess == null) {
    //                logger.info"Not exists connection for op: " + op);
                } else {
                    if (op.code() == Op.DISCONNECT) {
                        sess.close = true;
                    }
                    write(sess.out, op);
                }
            }

            selector.keys().forEach(key -> {
                if (key.isValid() && key.channel() instanceof SocketChannel) {
                    logger.info(key.attachment() + " OP_WRITE");
                    //todo check session
                    //may be write here
                    key.interestOps(OP_READ | OP_WRITE);
                }
            });
            responses.ops.clear();
        }


        private void write(ByteBuffer bb, OutOp op) {
            bb.put(op.code());
            int pos = bb.position();
            bb.position(pos + 1);
            op.write(bb);
            byte opLength = (byte) (bb.position() - pos - 1);
            bb.put(pos, opLength);//write the length
        }



        private void write(SelectionKey key) throws IOException {
            var session = (Session) key.attachment();
            var out = session.out;

            if (out.position() > 0) {
                //logger.info("Writing... " + session);
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
            var read = ch.read(in);//todo handle  SocketException

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
    */
    private void accept(SelectionKey key, Selector selector) throws IOException {
        var ch = ((ServerSocketChannel) key.channel()).accept();
        ch.configureBlocking(false);
//        var session = new Session(inc.incrementAndGet(), ch.getRemoteAddress());
        var gc = new GameChannel(key, ch);
        ch.register(selector, OP_READ, gc);
        sessions.register(gc);
//        listener.accept(gc, TRUE);
        // logger.info("Accepted: " + session);
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

    public static void run(Sessions sessions) {

        new Thread(() -> {
            try {
                new GameServer(sessions).start(6666);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "GameServer").start();


    }
}


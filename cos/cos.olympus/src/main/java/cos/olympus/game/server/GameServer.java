package cos.olympus.game.server;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.Responses;
import cos.ops.Op;
import cos.ops.AnyOp;
import cos.ops.Login;
import cos.ops.Move;
import cos.ops.OutOp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;

public final class GameServer {

    private final static Logger                 logger         = new Logger(GameServer.class);
    private              HashSet<SocketChannel> activeChannels = new HashSet<>();
    private final        DoubleBuffer<AnyOp>    actionsBuffer;
    private final        Responses              responses;
    private              ByteBuffer             responseBuffer = ByteBuffer.allocate(1024);
    volatile private     boolean                running        = true;
    volatile private     int                    id             = 0;

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
                if (!key.isValid()) continue;


//                logger.info(" next: " + key);
//                logger.info("isAcceptable=" + key.isAcceptable() + ", isReadable=" + key.isReadable() + "  isConnectable=${key.isConnectable}  isValid=${key.isValid}  isWritable=${key.isWritable}"
//                );

                if (key.isAcceptable()) {
                    accept(key, selector);
                } else if (key.isReadable()) {
                    logger.info("Reading: " + key);
                    read(key);
                } else if (key.isWritable()) {
//                    logger.info("isWritable");
                    if (responseBuffer.hasRemaining()) {
                        logger.info("Writing...");
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        responseBuffer.flip();
                        socketChannel.write(responseBuffer);
                    }

                }

            }


            readResponses();

//            logger.info("...");
        }
    }

    private void readResponses() {
        if (responses.ops.isEmpty()) return;

        logger.info("Writing ops to buffer ...");

        responseBuffer.clear();
        for(OutOp op: responses.ops) {
            logger.info("Write op: " + op);
        }
        responseBuffer = responses.flush();

//        logger.info("Wrote ops: " + Arrays.toString(responseBuffer.array()));
//        responseBuffer.rewind();

//        activeChannels.forEach(c -> {
//            logger.info("" + c);
//            logger.info("" + responseBuffer);
//            try {
//                c.write(responseBuffer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        var ch = ((ServerSocketChannel) key.channel()).accept();
        ch.configureBlocking(false);
        ch.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        logger.info("Connection accepted: " + ch.getRemoteAddress());
        activeChannels.add(ch);
//        ByteBuffer buffer = ByteBuffer.allocate(1024); ////
//        key.attach(buffer);
    }

    private void read(SelectionKey key) throws IOException {
        var ch = (SocketChannel) key.channel();
        var buf = ByteBuffer.allocate(32);
        ch.read(buf);

        if (buf.get(0) == Op.NOPE) {
            logger.info("No Data, close it");
            ch.close();
            key.cancel();
            return;
        }

        buf.flip();
        while (buf.remaining() > 0) {
            var op = parseOp(buf);
            logger.info("Op: " + op);
            if (op != null) {
                actionsBuffer.add(op);
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.write(responseBuffer);
        key.interestOps(SelectionKey.OP_READ);
    }

    private ServerSocketChannel setupSocket(int port, Selector selector) throws IOException {
        var socket = ServerSocketChannel.open();
        socket.configureBlocking(false);
        //set some options
        socket.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        socket.bind(new InetSocketAddress(port));
        socket.register(selector, SelectionKey.OP_ACCEPT);
        return socket;
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


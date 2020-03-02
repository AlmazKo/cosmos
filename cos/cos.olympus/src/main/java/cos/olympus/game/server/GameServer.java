package cos.olympus.game.server;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.game.Op;
import cos.olympus.ops.AnyOp;
import cos.olympus.ops.Login;
import cos.olympus.ops.Move;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public final class GameServer {

    private final static Logger logger = new Logger(GameServer.class);

    private final    DoubleBuffer<AnyOp> actionsBuffer;
    volatile private boolean             running = true;
    volatile private int                 id      = 0;

    public GameServer(DoubleBuffer<AnyOp> actionsBuffer) {
        this.actionsBuffer = actionsBuffer;
    }


    void start(int port) throws IOException, InterruptedException {

        var selector = Selector.open();
        var socket = ServerSocketChannel.open();


        socket.configureBlocking(false);
        //set some options
        socket.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        socket.bind(new InetSocketAddress(port));
        socket.register(selector, SelectionKey.OP_ACCEPT);
        //        log.atInfo().log("Waiting for connections ...");

        while (running) {
            logger.info(" i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();
            // token representing the registration of a SelectableChannel with a Selector

            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;

                logger.info(" next: $key");
                logger.info("isAcceptable=${key.isAcceptable} isReadable=${key.isReadable}  isConnectable=${key.isConnectable}  isValid=${key.isValid}  isWritable=${key.isWritable}"
                );
                // Tests whether this key's channel is ready to accept a new socket connection
                if (key.isAcceptable()) {
                    var client = socket.accept();
                    // Adjusts this channel's blocking mode to false
                    client.configureBlocking(false);
                    // Operation-set bit for read operations
                    client.register(selector, SelectionKey.OP_READ);
                    logger.info("Connection Accepted: " + client.getRemoteAddress());
                    // Tests whether this key's channel is ready for reading
                } else if (key.isReadable()) {
                    var client = (SocketChannel) key.channel();
                    var buf = ByteBuffer.allocate(32);
                    client.read(buf);


                    if (buf.get(0) == Op.NOPE) {
                        logger.info("No Data, close it");
                        client.close();
                        continue;
                    }
                    buf.flip();


                    //                    logger.info(Arrays.toString(buf.array()))
                    while (buf.remaining() > 0) {
                        //                        bb.getByte()
                        var op = parseOp(buf);
                        logger.info("Op: " + op);
                        if (op != null) {
                            actionsBuffer.add(op);
                        }
                    }


                }

            }
            Thread.sleep(300);
        }

    }


    private AnyOp parseOp(ByteBuffer b) {
        switch (b.get()) {
            case Op.LOGIN -> {
                return new Login(b);
            }
            case Op.MOVE -> {
                return new Move(b);
            }
//            case Op.FINISH -> {return null}
            default -> {
                return null;
            }
        }
    }


    public static void run(DoubleBuffer<AnyOp> buffer) {

        new Thread(() -> {
            try {
                new GameServer(buffer).start(6666);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "GameServer").start();


    }
}


package fx.nio;


import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Function;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

import cos.logging.Logger;

public final class Server implements Runnable {
    private final static Logger logger = Logger.get(Server.class);
    private final SocketAddress address;
    private final Function<SocketChannel, ReadChannel> handler;
    private volatile boolean running = true;

    public Server(SocketAddress address, Function<SocketChannel, ReadChannel> controller) {
        this.address = address;
        this.handler = controller;
    }

    @Override
    public void run() {
        try {
            start(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void start(SocketAddress address) throws IOException {
        var selector = setupServerSocket(address);
        logger.info("Server started on " + address);

        while (selector.isOpen() && running) {
            try {
                if (selector.select(500) == 0) continue;
            } catch (IOException ex) {
                ex.printStackTrace();
                // handle exception
                break;
            }

            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();
//                logger.info(toStr(key));
                try {
                    if (!key.isValid()) {
                        logger.warn("NOT isValid");
                    } else if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        ((ReadChannel) key.attachment()).read();
                    }
                } catch (Exception e) {
                    logger.warn("Wrong key " + key, e);
//                    if (key.attachment() != null) {
//                        ((Ch) key.attachment()).onDisconnect();
//                    }
//                    key.cancel();
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        var socketChannel = ((ServerSocketChannel) key.channel()).accept();
        //todo check quotas
        socketChannel.configureBlocking(false);
        socketChannel.socket().setTcpNoDelay(true);
        socketChannel.socket().setKeepAlive(true);

        var object = handler.apply(socketChannel);
        socketChannel.register(key.selector(), OP_READ, object);
//        logger.info("Accepted: " + socketChannel.getRemoteAddress());
    }

    private static Selector setupServerSocket(SocketAddress address) throws IOException {
        var selector = Selector.open();
        var server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.bind(address);
        server.register(selector, OP_ACCEPT);
        return selector;
    }
}


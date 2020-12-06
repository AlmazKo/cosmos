package cos.olympus.game.server;

import cos.logging.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public final class Server {
    private final        Sessions sessions;
    volatile             boolean  running = true;
    private final static Logger   logger  = new Logger(Server.class);

    public Server(Sessions sessions) {
        this.sessions = sessions;
    }

    void start(final int port) throws IOException {
        var address = new InetSocketAddress(port);
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
                //logger.info(toStr(key));
                try {
                    if (!key.isValid()) {
                        logger.warn("NOT isValid");
                    } else if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        ((GameChannel) key.attachment()).read();
                    }
                } catch (Exception e) {
                    logger.warn("Wrong key " + key, e);
//                    if (key.attachment() != null) {
//                        ((GameChannel) key.attachment()).onDisconnect();
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

        var gc = new GameChannel(socketChannel);
        socketChannel.register(key.selector(), OP_READ, gc);
        sessions.register(gc);
        //logger.info("Accepted: " + socketChannel.getRemoteAddress());
    }

    private Selector setupServerSocket(SocketAddress address) throws IOException {
        var selector = Selector.open();
        var server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.bind(address);
        server.register(selector, OP_ACCEPT);
        return selector;
    }

    private static String toStr(SelectionKey key) {
        return new StringBuilder(4)
                .append(key.isConnectable() ? 'C' : '-')
                .append(key.isReadable() ? 'R' : '-')
                .append(key.isWritable() ? 'W' : '-')
                .append(key.isAcceptable() ? 'A' : '-')
                .toString();
    }

    public static void run(Sessions sessions) {
        new Thread(() -> {
            try {
                new Server(sessions).start(6666);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Server").start();
    }
}


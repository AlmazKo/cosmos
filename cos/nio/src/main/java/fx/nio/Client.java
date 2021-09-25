package fx.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import static java.nio.channels.SelectionKey.OP_CONNECT;

import cos.logging.Logger;

public class Client {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final static Logger logger = Logger.get(Client.class);
    private final Function<SocketChannel, ReadChannel> handler;
    private volatile boolean running = true;

    public Client(Function<SocketChannel, ReadChannel> controller) {
        this.handler = controller;
    }

    private static Selector setupClientSocket(SocketAddress address) throws IOException {
        var selector = Selector.open();
        var client = SocketChannel.open();
        client.configureBlocking(false);
        client.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        client.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.connect(address);
        client.register(selector, OP_CONNECT);
        return selector;
    }


    public static void run(String host, int port, Function<SocketChannel, ReadChannel> handler) {
        var address = new InetSocketAddress(host, port);
        EXECUTOR.submit(() -> {
            try {
                new Client(handler).start(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void start(SocketAddress address) throws IOException {
        var selector = setupClientSocket(address);
        logger.info("Client connected to " + address);


        while (selector.isOpen() && running) {

            try {
                if (selector.select(500) == 0) continue;
            } catch (IOException ex) {
                ex.printStackTrace();// handle exception
                break;
            }

            var keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();
               // logger.info("Key " + toStr(key));
                if (key.isConnectable()) {
                    connect(key);
                } else if (key.isReadable()) {
                    ((ReadChannel) key.attachment()).read();
                }
            }
        }


    }


    void connect(SelectionKey key) throws IOException {

        var client = (SocketChannel) key.channel();
        if (client.finishConnect()) {
            client.configureBlocking(false);
            var object = handler.apply(client);
            client.register(key.selector(), SelectionKey.OP_READ, object);
        }
    }


//TODO    public void shutDown() {
}

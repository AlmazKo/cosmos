package fx.nio;


import fx.nio.codecs.BufferReadable;

import java.net.InetSocketAddress;
import java.util.function.Function;

public final class ServerLauncher {

    public static <I, O> void run(String host, int port, Function<RawChannel, BufferReadable> codecFactory) {
        var address = new InetSocketAddress(host, port);
        var server = new Server(address, (socket) -> {
            var channel = new RawChannel(socket);
            var codec = codecFactory.apply(channel);
            channel.register(codec);
            return channel;
        });

        var thread = new Thread(server, "Server:" + port);
        thread.start();
    }

}


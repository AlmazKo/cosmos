package fx.nio;


import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Function;

import fx.nio.codecs.Codec;

public final class ServerLauncher {

    public static <I, O> void run(String host, int port, Function<RawChannel, Codec<I, O>> codecFactory, Consumer<Codec<I, O>> handler) {
        var address = new InetSocketAddress(host, port);
        var server = new Server(address, (socket) -> {
            var channel = new RawChannel(socket);
            var codec = codecFactory.apply(channel);
            channel.register(codec);
            handler.accept(codec);
            return channel;
        });

        var thread = new Thread(server, "Server:" + port);
        thread.start();
    }

}


package cos.olympus.game.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

class Session {
    int userId = 0;
    final int           id;
    final SocketAddress remoteAddress;
    final ByteBuffer    in;
    final ByteBuffer    out;

    Session(int id, SocketAddress remoteAddress) {
        this.id = id;
        this.remoteAddress = remoteAddress;
        this.in = ByteBuffer.allocate(4096);
        this.out = ByteBuffer.allocate(4096);
        this.out.flip();
    }

    @Override public String toString() {
        return "Session{" +
                "id=" + id +
                ", remoteAddress=" + remoteAddress +
                ", in=" + in +
                ", out=" + out +
                '}';
    }
}

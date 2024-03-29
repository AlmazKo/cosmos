package fx.nio;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;

final class Session {
    public final static AtomicInteger INC = new AtomicInteger();
    public final int id;
    public final SocketAddress remoteAddress;
    public final ByteBuffer in;
    public final ByteBuffer out;
    public final long start = currentTimeMillis();
    int userId = 0;

    Session(int id, SocketAddress remoteAddress) {
        this.id = id;
        this.remoteAddress = remoteAddress;
        this.in = ByteBuffer.allocate(34 * 1024);//todo: tmp
        this.out = ByteBuffer.allocate(34 * 1024);//todo: tmp
    }

    @Override
    public String toString() {
        return "Session{" +
               "id=" + id +
               ", remoteAddress=" + remoteAddress +
//                ", in=" + in +
//                ", out=" + out +
               ", timeout=" + (currentTimeMillis() - start) +
               "ms}";
    }

    String toShortString() {
        return "#" + userId + ", " + remoteAddress;
    }
}

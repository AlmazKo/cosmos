package cos.olympus.game.api;

import cos.olympus.util.OpsConsumer;
import cos.ops.AnyOp;
import cos.ops.out.UserPackage;

import java.util.ArrayList;
import java.util.List;

public final class Connections {
    private final ArrayList<Connection> connections = new ArrayList<>();

    //call from Game
    public List<AnyOp> collect() {
        var result = new ArrayList<AnyOp>();
        connections.forEach(s -> s.collect(result));
        return result;
    }

    //call from Game
    public void write(int tick, OpsConsumer oc) {
        connections.forEach(conn -> {
            try {
                oc.data.forEach((userId, ops) -> {
                    var up = new UserPackage(tick, userId, ops.toArray(new Record[0]));
                    conn.write(up);
                });

            } catch (Exception e) {
                e.printStackTrace();
                //fixme : todo something with full buffers
            }
        });

        connections.forEach(Connection::flush);
        connections.removeIf(Connection::isFinished);
    }

    //call from Server
    public void register(Connection gc) {
        connections.add(gc);
    }
}

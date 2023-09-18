package cos.olympus.game.api;

import cos.olympus.util.OpsConsumer;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.out.UserPackage;

import java.util.ArrayList;
import java.util.List;

public final class Connections {
    private final ArrayList<UserOp> ins = new ArrayList<>();
    public final ArrayList<UserPackage> out = new ArrayList<>();



    public void in(UserOp record) {
        ins.add(record);
    }

    //call from Game
    public List<UserOp> collect() {
        List<UserOp> result;
        synchronized (ins) {
            if (ins.isEmpty()) return List.of();
            result = List.copyOf(ins);
            ins.clear();
        }
        return result;
    }

    //call from Game
    public void write(int tick, OpsConsumer oc) {
        try {
            oc.data.forEach((userId, ops) -> {
                if (userId == 0) {
                    for (SomeOp o : ops) {
                      //TODO  System.out.println(">> " + o);


//                            conn.write((Record) o);
                    }
                } else {
                    var op = new UserPackage(tick, userId, ops.toArray(new Record[0]));
                    out.add(op);

//                        conn.write(op);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //fixme : todo something with full buffers
        }

//        connections.forEach(Connection::flush);
//        connections.removeIf(Connection::isFinished);
    }
}

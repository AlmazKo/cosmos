package cos.olympus.game.server;

import cos.olympus.util.OpsConsumer;
import cos.ops.AnyOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Sessions {

    private final Map<Integer, GameChannel> channels     = new HashMap<>();
    private final ArrayList<GameChannel>    protoSession = new ArrayList<>();

    //call from Game
    public List<AnyOp> collect() {
        protoSession.removeIf(proto -> {
                    if (proto.userId() != 0) {
                        channels.put(proto.userId(), proto);
                        return true;
                    }
                    return false;
                }
        );
        var result = new ArrayList<AnyOp>();
        channels.values().forEach(s -> s.collect(result));
        return result;
    }

    //call from Game
    public void write(OpsConsumer ops) {
        ops.data.forEach(oo -> {
            //todo NPE
            var gc = channels.get(oo.userId());
            if (gc != null) gc.write(oo);
        });

        channels.values().forEach(GameChannel::flush);
        channels.values().removeIf(GameChannel::isFinished);
    }


    //call from Server
    public void register(GameChannel gc) {
        protoSession.add(gc);
    }
}

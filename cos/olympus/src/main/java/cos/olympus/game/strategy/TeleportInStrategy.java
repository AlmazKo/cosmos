package cos.olympus.game.strategy;

import cos.olympus.game.Game;
import cos.olympus.game.Player;
import cos.olympus.util.OpConsumer;
import cos.ops.out.ProtoAppear;
import cos.ops.out.TeleportIn;

public class TeleportInStrategy implements Strategy {
    private final Game to;
    private final int respawnTime;
    private final TeleportIn t;

    public TeleportInStrategy(int tick, TeleportIn t, Game to) {
        this.to = to;
        this.t = t;
        this.respawnTime = tick;
    }

    @Override
    public boolean onTick(int tick, OpConsumer out) {
        if (tick >= respawnTime) {
            var avatar = new Player(t.userId(), "user:" + t.id());
            var cr = to.getWorld().place(avatar, t.x(), t.y(), 100, 4);//move metrics
            var op = new ProtoAppear(1, tick, avatar.id(), to.getWorld().getName(), cr.x(), cr.y(), cr.sight());
            out.add(op);
            return true;
        }
        return false;
    }
}

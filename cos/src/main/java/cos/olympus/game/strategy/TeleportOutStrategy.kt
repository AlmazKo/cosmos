package cos.olympus.game.strategy;

import cos.map.PortalSpot;
import cos.olympus.game.Agent;
import cos.olympus.game.Game;
import cos.olympus.util.OpConsumer;
import cos.ops.out.TeleportIn;

public class TeleportOutStrategy implements Strategy {
    private final Game game;
    private final Agent avatar;
    private final int respawnTime;
    private final String to;
    private final int toX;
    private final int toY;
    private int state = 0;

    public TeleportOutStrategy(int tick, Game from, Agent avatar, PortalSpot spot) {
        this.game = from;
        this.avatar = avatar;
        this.respawnTime = tick + 1;
        this.to = spot.map();
        this.toX = spot.dstX();
        this.toY = spot.dstY();
    }

    @Override
    public boolean onTick(int tick, OpConsumer out) {
        if (state == 0) {
            game.removeAvatar(avatar.id());
            //todo: add event
            state = 1;
            return false;
        } else if (state == 1 && tick >= respawnTime) {
            var a = avatar;
            out.add(new TeleportIn(100500, tick, a.id(), to, toX, toY, a.sight()));
            state = 2;
            return true;
        }
        return false;
    }
}

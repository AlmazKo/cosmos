package cos.olympus.game.strategy;

import cos.olympus.game.Game;
import cos.olympus.game.Player;
import cos.olympus.game.Usr;
import cos.olympus.util.OpConsumer;
import cos.ops.out.ProtoAppear;

import java.util.Map;

public class LoginStrategy implements Strategy {
    private final Map<String, Game> games;
    private final Usr usr;

    public LoginStrategy(Map<String, Game> games, Usr usr) {
        this.games = games;
        this.usr = usr;
    }

    @Override
    public boolean onTick(int tick, OpConsumer outOps) {
        var world = games.get(usr.worldName).getWorld();
        var player = new Player(usr.id, usr.name);
        var creature = world.place(player, 0, 0, 100, 4);
        var op = new ProtoAppear(1, tick, usr.id, "map", creature.x(), creature.y(), creature.sight());
        outOps.add(op);
        return true;
    }

}

package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.Strategy;
import cos.olympus.util.OpConsumer;
import cos.ops.out.ProtoAppear;

import java.util.HashMap;
import java.util.Map;

public class LoginStrategy implements Strategy {
    private final static Logger logger = Logger.get(LoginStrategy.class);

    private final HashMap<Integer, Player> users = new HashMap<>();
    private final Map<String, Game> games;
    private final int userId;

    public LoginStrategy(Map<String, Game> games, int userId) {
        this.games = games;
        this.userId = userId;
    }

    @Override
    public boolean onTick(int tick, OpConsumer outOps) {
        var usr = users.get(userId);
        if (usr == null) {
            var worldName = "map";
            var world = games.get(worldName).getWorld();
            usr = new Player(userId, "user:" + userId);
            var creature = world.createCreature(usr, 100, 4);
            var op = new ProtoAppear(1, tick, usr.id, "map", creature.x, creature.y, creature.sight);
            outOps.add(op);
        } else {
            logger.warn("#" + tick + " " + "User already logged in " + usr);
        }

        return true;
    }

}

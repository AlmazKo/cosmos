package cos.olympus.game;

import cos.logging.Logger;
import cos.logging.ThreadContext;
import cos.olympus.game.strategy.LoginStrategy;
import cos.olympus.game.strategy.Strategy;
import cos.olympus.game.strategy.TeleportInStrategy;
import cos.olympus.util.OpConsumer;
import cos.ops.ServiceOp;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.in.Login;
import cos.ops.out.AllCreatures;
import cos.ops.out.TeleportIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaGame {
    private final static Logger LOG = Logger.get(Game.class);
    private final Map<Integer, Usr> users = new HashMap<>();
    private final Map<String, Game> games;
    private final List<Strategy> strategies = new ArrayList<>();

    public MetaGame(Map<String, Game> games) {
        this.games = games;
    }

    public void onTick(int tick, List<UserOp> userOps, List<ServiceOp> serviceOps, OpConsumer out) {
        ThreadContext.set("SUB_TYPE", "#" + tick);

        serviceOps.forEach(op -> {
            if (op instanceof TeleportIn t) {
                var target = games.get(t.world());
                strategies.add(new TeleportInStrategy(tick, t, target));
            }
        });

        userOps.forEach(op -> {
            if (op instanceof Login) {
//                strategies.add(new LoginStrategy(games, op.userId()));

                onLogin(tick, (Login) op);

            } else {
                games.values().forEach(game -> game.handleIncomeOp(op));
            }
        });

        games.values().forEach(game -> {
            game.onTick(tick, out);
            collectMetrics(out, game);
        });

        strategies.removeIf(strategy -> strategy.onTick(tick, out));
    }

    private void collectMetrics(OpConsumer out, Game game) {
        var crs = game.getWorld().getAllCreatures();
        if (crs.isEmpty()) return;

        int i = 0;
        var data = new int[crs.size() * 3];
        for (Creature cr : crs) {
            data[i++] = cr.x;
            data[i++] = cr.y;
            data[i++] = cr.type().ordinal();
        }
        var w = game.getWorld();
        var op = new AllCreatures(w.width, w.height, w.offsetX, w.offsetY, data);
        out.add(op);
    }

    public void onLogin(int tick, Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Usr(op.userId(), "map");
            users.put(op.userId(), usr);
            LOG.info("#" + tick, "New User " + usr);
            strategies.add(new LoginStrategy(games, usr));
        }
    }

}

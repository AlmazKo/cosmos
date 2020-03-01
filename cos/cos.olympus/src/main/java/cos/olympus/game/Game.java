package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.ops.AnyOp;
import cos.olympus.ops.Login;
import cos.olympus.ops.Move;

import java.util.HashMap;

;

public final class Game {
    private final static Logger                     logger    = new Logger(Game.class);
    private final        GameMap                    map;
    private final        DoubleBuffer<AnyOp>        bufferOps;
    private final        Movements                  movements;
    private final        HashMap<Integer, User>     users     = new HashMap();
    private final        HashMap<Integer, Creature> creatures = new HashMap();

    int id = 0;

    public Game(GameMap map, DoubleBuffer<AnyOp> bufferOps) {
        this.map = map;
        this.bufferOps = bufferOps;
        this.movements = new Movements(map);
    }


    public void onTick(int id, long tsm) {
        var ops = bufferOps.getAndSwap();

        movements.update();
        if (!ops.isEmpty()) logger.info("" + ops.size() + " ops");

        ops.forEach((op) -> {
            if (op instanceof Login) {
                onLogin((Login) op);
            } else if (op instanceof Move) {
                onMove((Move) op);
            }
            ;
        });
    }

    private void onLogin(Login op) {

        var usr = users.get(op.userId);
        if (usr == null) {
            usr = new User(op.userId, "user:" + op.userId);
            var creature = map.createCreature(usr);
            creatures.put(creature.id, creature);
            logger.info("Placed" + creature);
        } else {

            logger.warn("User already logged in $usr");
        }

    }

    private void onMove(Move op) {
        var cr = creatures.get(op.userId);
        if (cr == null) return;

        movements.start(cr, op);
    }
}


package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.ops.AnyOp;
import cos.ops.Arrival;
import cos.ops.Disconnect;
import cos.ops.Login;
import cos.ops.Move;
import cos.ops.OutOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class Game {
    private final static Logger                     logger    = new Logger(Game.class);
    private final        GameMap                    map;
    private final        DoubleBuffer<AnyOp>        bufferOps;
    private final        Movements                  movements;
    private final        HashMap<Integer, User>     users     = new HashMap();
    private final        HashMap<Integer, Creature> creatures = new HashMap();

    private final ArrayList<OutOp> outOps = new ArrayList<>();

    int id = 0;
    private int tick = 0;

    public Game(GameMap map, DoubleBuffer<AnyOp> bufferOps) {
        this.map = map;
        this.bufferOps = bufferOps;
        this.movements = new Movements(map);
    }


    public List<OutOp> onTick(int id, long tsm) {
        tick = id;
        outOps.clear();
        var ops = bufferOps.getAndSwap();

        movements.update();
        if (!ops.isEmpty()) logger.info("" + ops.size() + " ops");

        ops.forEach((op) -> {
            try {
                if (op instanceof Login) {
                    onLogin((Login) op);
                } else if (op instanceof Move) {
                    onMove((Move) op);
                }
            } catch (Exception ex) {
                logger.warn("Error during processing ", ex);
                outOps.add(new Disconnect(op.id(),tick, op.userId()));
            }

        });

        return outOps;
    }

    private void onLogin(Login op) {

        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new User(op.userId(), "user:" + op.userId());
            var creature = map.createCreature(usr);
            creatures.put(creature.id, creature);
            logger.info("Placed " + creature);
            outOps.add(new Arrival(op.id(), tick, usr.id, creature.x, creature.y, creature.dir, creature.sight));
        } else {

            logger.warn("User already logged in " + usr);
        }

    }

    private void onMove(Move op) {
        var cr = creatures.get(op.userId());
        if (cr == null) return;

        movements.start(cr, op);
    }
}


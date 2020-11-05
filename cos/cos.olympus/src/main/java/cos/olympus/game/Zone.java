package cos.olympus.game;

import cos.logging.Logger;
import cos.ops.CreatureMoved;
import cos.ops.ObjAppear;
import cos.ops.OutOp;

import java.util.Collection;

public class Zone {

    private final static Logger logger = new Logger(Game.class);

    private final GameMap map;

    public Zone(GameMap map) {
        this.map = map;
    }

    void calc(Creature target, int tick, Collection<OutOp> consumer) {
        //todo hardcode radius
        map.iterate(target.x, target.y, 8, (x, y) -> {
            var o = map.getObject(x, y);
            if (o != null && !target.zoneObjects.containsKey(o.id())) {
                target.zoneObjects.put(o.id(), o);
                consumer.add(new ObjAppear(o.id(), tick, target.id, x, y, o.tileId()));
            }

            if (target.x == x && target.y == y) return; // avoid self-detection

            var cr = map.getCreature(x, y);


            if (cr == null) {
                //disappear or /nothing
            } else {
                var ort = target.zoneCreatures.get(cr.id);
                if (ort == null || (ort.x() != cr.x || ort.y() != cr.y) || ort.speed() !=cr.speed) {
                    target.zoneCreatures.put(cr.id, cr.orientation());
                    consumer.add(new CreatureMoved(1, tick, target.id, cr.id, x, y, cr.speed, cr.mv, cr.sight));
                }
            }
        });
    }
}

package cos.olympus.game;

import cos.ops.ObjAppear;
import cos.ops.OutOp;

import java.util.Collection;

public class Zone {

    private final GameMap map;

    public Zone(GameMap map) {
        this.map = map;
    }

    void calc(Creature cr, int tick, Collection<OutOp> consumer) {
        //todo hardcode radius
        map.iterate(cr.x, cr.y, 2, (x, y) -> {
            var o = map.getObject(x, y);
            if (o != null && !cr.zoneObjects.containsKey(o.id())) {
                cr.zoneObjects.put(o.id(), o);
                consumer.add(new ObjAppear(o.id(), tick, cr.id, x, y, o.tileId()));
            }
        });
    }
}

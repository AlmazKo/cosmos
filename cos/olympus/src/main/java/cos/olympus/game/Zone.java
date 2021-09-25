package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.util.OpConsumer;
import cos.ops.out.CreatureHid;
import cos.ops.out.CreatureMoved;
import cos.ops.out.Metrics;
import cos.ops.out.ObjAppear;

public class Zone {

    private final static Logger logger = Logger.get(Zone.class);
    private final static int    radius = 8;

    private final World world;

    public Zone(World world) {
        this.world = world;
    }

    void onTick(Creature target, int tick, OpConsumer consumer) {
        //todo hardcode radius
        world.iterate(target.x, target.y, radius, (x, y) -> {
            var o = world.getObject(x, y);
            if (o != null && !target.zoneObjects.containsKey(o.id())) {
                target.zoneObjects.put(o.id(), o);
                consumer.add(new ObjAppear(o.id(), tick, target.id(), x, y, o.tile().id()));
            }

//            if (target.x == x && target.y == y) return; // avoid self-detection

            Creature cr;
            if (target.x == x && target.y == y) {
                cr = target;
            } else {
                cr = world.getCreature(x, y);
            }


            if (cr == null) {
                //disappear or /nothing
            } else {
                var ort = target.zoneCreatures.get(cr.id());
                if (ort == null || (ort.x() != cr.x || ort.y() != cr.y) || ort.speed() != cr.speed || ort.sight() != cr.sight) {
                    target.zoneCreatures.put(cr.id(), cr.orientation());
                    consumer.add(new CreatureMoved(1, tick, target.id(), cr.id(), x, y, cr.offset, cr.speed, cr.mv, cr.sight));
                }

                var met = target.zoneMetrics.get(cr.id());
                if (met == null || (met.life() != cr.life() || met.maxLife() != cr.metrics.maxLife())) {
                    var n = cr.copyMetrics();
                    target.zoneMetrics.put(cr.id(), n);
                    consumer.add(new Metrics(1, tick, target.id(), cr.id(), cr.level, n.life(), n.maxLife()));
                }
            }

        });

        target.zoneCreatures.values().removeIf(ort -> {
            if (ort.creatureId() == target.id()) return false;

            var cr = world.getCreature(ort.creatureId());
            if (cr == null || inNotFov(target, cr)) {
                consumer.add(new CreatureHid(1, tick, target.id(), ort.creatureId()));
                target.zoneMetrics.remove(ort.creatureId());
                return true;
            } else {
                return false;
            }
        });
    }

    static boolean inNotFov(Creature target, Creature o) {
        return Math.abs(target.x - o.x) > radius || Math.abs(target.y - o.y) > radius;
    }
}

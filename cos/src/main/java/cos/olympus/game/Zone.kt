package cos.olympus.game

import cos.logging.Logger
import cos.olympus.util.OpConsumer
import cos.ops.out.CreatureHid
import cos.ops.out.CreatureMoved
import cos.ops.out.Metrics
import cos.ops.out.ObjAppear
import kotlin.math.abs

class Zone(private val world: World) {
    fun onTick(target: Creature, tick: Int, consumer: OpConsumer) {
        //todo hardcode radius
        world.iterateAround(target.x, target.y, VIEW_RADIUS) { x, y ->
            val obj = world.getObject(x, y)
            if (obj != null && !target.zoneObjects.containsKey(obj.id)) {
                target.zoneObjects[obj.id] = obj
                consumer.add(ObjAppear(obj.id, tick, target.id, x, y, obj.tile.id))
            }

            //            if (target.getX() == x && target.getY() == y) return; // avoid self-detection
            val cr = if (target.x == x && target.y == y) {
                target
            } else {
                world.getCreature(x, y)
            }
            if (cr == null) {
                //disappear or /nothing
            } else {
                val ort = target.zoneCreatures[cr.id]
                if (ort == null || (ort.x != cr.x || ort.y != cr.y) || ort.speed != cr.speed || ort.sight != cr.sight) {
                    target.zoneCreatures[cr.id] = cr.orientation()
                    consumer.add(CreatureMoved(1, tick, target.id, cr.id, x, y, cr.offset, cr.speed, cr.mv, cr.sight))
                }

                val met = target.zoneMetrics[cr.id]
                if (met == null || (met.life() != cr.life || met.maxLife() != cr.metrics.maxLife() || met.exp != cr.metrics.exp)) {
                    val n = cr.copyMetrics()
                    target.zoneMetrics[cr.id] = n
                    consumer.add(Metrics(1, tick, target.id, cr.id, cr.metrics.lvl, cr.metrics.exp, n.life(), n.maxLife()))
                }
            }
        }

        target.zoneCreatures.values.removeIf { ort: Orientation ->
            if (ort.creatureId == target.id) return@removeIf false
            val cr = world.getCreature(ort.creatureId)
            if (cr == null || inNotFov(target, cr)) {
                consumer.add(CreatureHid(1, tick, target.id, ort.creatureId))
                target.zoneMetrics.remove(ort.creatureId)
                return@removeIf true
            } else {
                return@removeIf false
            }
        }
    }

    companion object {
        private val logger: Logger = Logger.get(Zone::class.java)
        private const val VIEW_RADIUS = 8

        fun inNotFov(target: Creature, o: Creature): Boolean {
            return abs((target.x - o.x).toDouble()) > VIEW_RADIUS || abs((target.y - o.y).toDouble()) > VIEW_RADIUS
        }
    }
}

package cos.olympus.game

import cos.logging.Logger
import cos.olympus.util.OpConsumer
import cos.ops.out.Disappear
import cos.ops.out.Metrics
import cos.ops.out.Move
import cos.ops.out.Obj
import kotlin.math.abs

class Zone(private val world: World) {
    fun onTick(target: Actor, out: OpConsumer) {
        //todo hardcode radius
        world.iterateAround(target.x, target.y, VIEW_RADIUS) { x, y ->
            val obj = world.getObject(x, y)
            if (obj != null && !target.inZone(obj)) {
                target.addInZone(obj)
                out(Obj(obj.id, target.id, x, y, obj.tile.id))
            }

            val a = if (target.x == x && target.y == y) target else world.getActor(x, y)

            if (a == null) {
                //disappear or /nothing
            } else {
                val ort = target.zoneActors[a.id]
                if (ort == null || (ort.x != a.x || ort.y != a.y) || ort.speed != a.speed || ort.sight != a.sight) {
                    target.addInZone(a)
                    out(Move(1, target.id, a.id, x, y, a.offset, a.speed, a.mv, a.sight))
                }

                val met = target.zoneMetrics[a.id]
                if (a.metrics != met) {
                    val n = a.metrics.copy()
                    target.zoneMetrics[a.id] = n
                    out(Metrics(1, target.id, a.id, a.metrics.lvl, a.metrics.exp, n.life, n.maxLife))
                }
            }
        }

        target.zoneActors.values.removeIf { ort ->
            if (ort.actorId == target.id) return@removeIf false

            val a = world.getActor(ort.actorId)
            if (a == null || inNotFov(target, a)) {
                out(Disappear(1, target.id, ort.actorId))
                target.zoneMetrics.remove(ort.actorId)
                return@removeIf true
            } else {
                return@removeIf false
            }
        }
    }

    companion object {
        private val logger: Logger = Logger.get(Zone::class.java)
        private const val VIEW_RADIUS = 8

        fun inNotFov(target: Actor, o: Actor): Boolean {
            return abs((target.x - o.x).toDouble()) > VIEW_RADIUS || abs((target.y - o.y).toDouble()) > VIEW_RADIUS
        }
    }
}

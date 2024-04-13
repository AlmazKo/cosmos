package cos.olympus.game

import cos.logging.Logger
import cos.map.TileType
import cos.olympus.game.MapUtil.nextX
import cos.olympus.game.MapUtil.nextY
import cos.olympus.util.TimeUtil
import cos.ops.Direction
import cos.ops.`in`.Move
import cos.ops.`in`.StopMove

class Movements internal constructor(private val world: World) : TickAware {
    private val mvs = HashMap<Int, Mv>()

    fun onMove(op: Move) {
        val actor = world.getActor(op.userId) ?: return

        change(actor, op)
    }

    fun onStopMove(op: StopMove) {
        val actor = world.getActor(op.userId) ?: return

        stop(actor, op.sight)
    }

    private class Mv(val actor: Actor) {
        var next: Move? = null
        var stop: Boolean = false
    }

    fun changeSight(actor: Actor, sight: Direction?) {
        val mv = mvs[actor.id]
        if (mv != null) {
            //fixme this is simulation
            mv.next = Move(-1, -1, actor.x, actor.y, null, sight)
        } else {
            actor.sight = sight!!
        }
    }

    fun change(actor: Actor, op: Move) {
        val mv = mvs[actor.id]
        if (mv != null) {
            mv.next = op
        } else {
            if (op.dir == null) {
                actor.sight = op.sight
                return
            }

            mvs[actor.id] = Mv(actor)
            actor.mv = op.dir
            actor.sight = op.sight
        }

        val currentTile: TileType? = world[actor.x, actor.y]
        actor.speed = TimeUtil.toTickSpeed(getSpeed(currentTile))
    }

    fun interrupt(actor: Actor) {
        mvs.remove(actor.id)
    }

    fun stop(cr: Actor, sight: Direction?) {
        val mv = mvs[cr.id]
        if (mv != null) {
            mv.stop = true
        } else {
            cr.sight = sight!!
        }
    }

    override fun onTick(tickId: Int) {
        mvs.values.removeIf { mv: Mv ->
            val del = onTick(mv)
            del
        }
    }

    private fun onTick(mv: Mv): Boolean {
        val a = mv.actor
        val newOffset = a.offset + a.speed
        if (newOffset < METER) {
            a.offset = newOffset
            return false
        }

        //next cell
        val x = nextX(a)
        val y = nextY(a)

        if (cannotStep(a, x, y) || world.hasActor(x, y)) {
            a.offset = 0

            ///            logger.info(cr, "reset");
            if (mv.stop) {
                a.stop()
                ///                logger.info(cr, "finish");
                return true
            }
            return false
        }

        world.move(a, x, y)

        if (mv.stop) {
            a.stop()
            ///            logger.info(cr, "finish");
            return true
        } else {
            if (mv.next != null) {
                a.mv = mv.next!!.dir
                a.sight = mv.next!!.sight
                mv.next = null
            }

            a.offset = newOffset - METER
            val tile = world[x, y]
            a.speed = TimeUtil.toTickSpeed(getSpeed(tile))
            logger.info(a, "")
            return false
        }
    }

    private fun cannotStep(actor: Actor, x: Pos, y: Pos): Boolean {
        val obj = world.getObject(x, y)
        if (obj != null && obj.tile.type == TileType.WALL) {
            return true
        }
        val tile = world[x, y]
        return tile == TileType.NOTHING || tile == TileType.DEEP_WATER || tile == TileType.WALL
    }

    companion object {
        const val HALF: Int = 50
        const val METER: Int = 100
        private val logger: Logger = Logger.get(Movements::class.java)
        private fun getSpeed(currentTile: TileType?): Int {
            checkNotNull(currentTile) { "Null title" }

            return when (currentTile) {
                TileType.GRASS, TileType.SAND, TileType.TIMBER -> 400
                TileType.SHALLOW, TileType.GATE -> 100
                else -> throw IllegalStateException("Unsupported $currentTile")
            }
        }
    }
}

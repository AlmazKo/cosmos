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
        val cr = world.getCreature(op.userId) ?: return

        change(cr, op)
    }

    fun onStopMove(op: StopMove) {
        val cr = world.getCreature(op.userId) ?: return

        stop(cr, op.sight)
    }

    private class Mv(val cr: Creature) {
        var next: Move? = null
        var stop: Boolean = false
    }

    fun changeSight(cr: Creature, sight: Direction?) {
        val mv = mvs[cr.id]
        if (mv != null) {
            //fixme this is simulation
            mv.next = Move(-1, -1, cr.x, cr.y, null, sight)
        } else {
            cr.sight = sight!!
        }
    }

    fun change(cr: Creature, op: Move) {
        val mv = mvs[cr.id]
        if (mv != null) {
            mv.next = op
        } else {
            if (op.dir == null) {
                cr.sight = op.sight
                return
            }

            mvs[cr.id] = Mv(cr)
            cr.mv = op.dir
            cr.sight = op.sight
        }

        val currentTile: TileType? = world[cr.x, cr.y]
        cr.speed = TimeUtil.toTickSpeed(getSpeed(currentTile))
    }

    fun interrupt(cr: Creature) {
        mvs.remove(cr.id)
    }

    fun stop(cr: Creature, sight: Direction?) {
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
        val cr = mv.cr
        val newOffset = cr.offset + cr.speed
        if (newOffset < METER) {
            cr.offset = newOffset
            return false
        }

        //next cell
        val x = nextX(cr)
        val y = nextY(cr)

        if (cannotStep(cr, x, y) || world.hasCreature(x, y)) {
            cr.offset = 0

            ///            logger.info(cr, "reset");
            if (mv.stop) {
                cr.stop()
                ///                logger.info(cr, "finish");
                return true
            }
            return false
        }

        world.moveCreature(cr, x, y)

        if (mv.stop) {
            cr.stop()
            ///            logger.info(cr, "finish");
            return true
        } else {
            if (mv.next != null) {
                cr.mv = mv.next!!.dir
                cr.sight = mv.next!!.sight
                mv.next = null
            }

            cr.offset = newOffset - METER
            val tile = world[x, y]
            cr.speed = TimeUtil.toTickSpeed(getSpeed(tile))
            logger.info(cr, "")
            return false
        }
    }

    private fun cannotStep(cr: Creature, x: Int, y: Int): Boolean {
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

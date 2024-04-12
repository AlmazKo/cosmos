package cos.olympus.game.strategy

import cos.logging.Logger
import cos.olympus.Util
import cos.olympus.game.Creature
import cos.olympus.game.MapUtil.direction
import cos.olympus.game.MapUtil.nextX
import cos.olympus.game.MapUtil.nextY
import cos.olympus.game.Movements
import cos.olympus.game.Spells
import cos.olympus.game.World
import cos.olympus.util.OpConsumer
import cos.ops.Direction
import cos.ops.`in`.Move

class NpcStrategy(
    private val npc: Creature,
    private val world: World,
    private val spells: Spells,
    private val movements: Movements
) : Strategy {
    private var nextPlannedTick = -1

    override fun onTick(tick: Int, out: OpConsumer): Boolean {
        if (tick <= nextPlannedTick) return false

        if (!tryToAttract(tick)) {
            walkingAround()
            nextPlannedTick = tick + Util.rand(10, 20)
        }
        return false
    }

    private fun tryToAttract(tick: Int): Boolean {
        if (!npc.type.isAggressive) return false

        val nextX = nextX(npc)
        val nextY = nextY(npc)
        val near = world.getCreature(nextX, nextY)
        if (near != null && near.type != npc.type) {
///            logger.info("" + npc + " aggro-ed " + near);
            spells.onMeleeAttack(tick, npc)
            nextPlannedTick = tick + Util.rand(4, 8)
            return true
        }

        val dir = turnTo() ?: return false

        logger.info("$npc attracted $dir")
        movements.changeSight(npc, dir)
        nextPlannedTick = tick + 1
        return true
    }

    private fun turnTo(): Direction? {
        val nears = world.getCreatures(npc.x, npc.y, 1)

        for (near in nears) {
            if (near.type != npc.type) {
                val dir = direction(npc, near)
                if (dir != null) return dir
            }
        }

        return null
    }


    private fun walkingAround() {
        val dir = Direction.entries[Util.rand(0, 4)]
        val x = nextX(npc, dir)
        val y = nextY(npc, dir)

        if (world.isFree(x, y) && world.isNoMovingCreaturesIn(x, y)) {
            val mv = Move(0, npc.id, npc.x, npc.y, dir, dir)
            movements.change(npc, mv)
            movements.stop(npc, npc.sight)
        } else {
//                logger.info("Can not move to " + dir + " #" + npc.id);
//                logger.info("Cannot move to " + dir + " #" + npc.id
//                        + ", x=" + x + ", y=" + y + ", free=" + world.isFree(x, y) + ", smth stand=" + world.hasCreature(x, y));
        }
    }

    val isDead: Boolean
        get() = npc.isDead

    companion object {
        private val logger: Logger = Logger.get(NpcStrategy::class.java)
    }
}

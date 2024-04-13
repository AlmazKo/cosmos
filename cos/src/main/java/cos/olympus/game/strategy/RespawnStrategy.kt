package cos.olympus.game.strategy

import cos.logging.Logger
import cos.map.Coord
import cos.map.ActorType
import cos.olympus.NoSpaceException
import cos.olympus.Util
import cos.olympus.game.Movements
import cos.olympus.game.Npc
import cos.olympus.game.Spells
import cos.olympus.game.World
import cos.olympus.util.OpConsumer

class RespawnStrategy(
    private val world: World,
    private val spells: Spells,
    private val movements: Movements,
    private val spot: Coord,
    private val type: ActorType
) : Strategy {
    private var live: NpcStrategy? = null
    private var isDead = false
    private var respawnTime = Int.MAX_VALUE


    override fun onTick(tick: Int, out: OpConsumer): Boolean {
        if (live == null) {
            if (isDead && tick < respawnTime) {
                return false
            }
            isDead = false
            try {
                val npc = world.place(Npc(++id, type, "Phantom"), spot.x, spot.y, 80, 4)
                live = NpcStrategy(npc, world, spells, movements)
            } catch (ne: NoSpaceException) {
                logger.warn("No space")
                return false
            }
        }

        if (live!!.isDead) {
            live = null
            isDead = true
            respawnTime = tick + Util.rand(10, 30) //tmp
            return false
        }

        live!!.onTick(tick, out)
        return false
    }

    companion object {
        private val logger: Logger = Logger.get(Movements::class.java)
        private var id = 10000
    }
}

package cos.olympus.game.strategy

import cos.map.ActorType.PLAYER
import cos.olympus.NoSpaceException
import cos.olympus.Util
import cos.olympus.game.Npc
import cos.olympus.game.Player
import cos.olympus.game.TickId
import cos.olympus.game.World
import cos.olympus.util.OpConsumer
import cos.ops.out.Appear

class RespawnPlayerStrategy(
    tick: TickId,
    private val world: World,
    private val player: Player
) : Strategy {

    private val respawnTime = tick + Util.rand(20, 40)

    override fun onTick(tick: TickId, outOps: OpConsumer): Boolean {
        if (tick < respawnTime) return false

        try {
            val a = world.place(Npc(player.id, PLAYER, player.name), 34, -24, 100, 1)
            outOps.add(Appear(0,  a.id, a.x, a.y, a.mv, a.sight, a.metrics.lvl, a.life))
        } catch (e: NoSpaceException) {
            return false
        }
        return true
    }
}

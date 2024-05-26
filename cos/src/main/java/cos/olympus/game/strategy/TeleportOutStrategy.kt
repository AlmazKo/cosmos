package cos.olympus.game.strategy

import cos.map.PortalSpot
import cos.olympus.game.Agent
import cos.olympus.game.Game
import cos.olympus.game.TickId
import cos.olympus.util.OpConsumer
import cos.ops.out.TeleportIn

class TeleportOutStrategy(
    tick: TickId,
    private val game: Game,
    private val avatar: Agent,
    spot: PortalSpot
) : Strategy {
    private val respawnTime = tick + 1
    private val to: String = spot.map
    private val toX = spot.dstX
    private val toY = spot.dstY
    private var state = 0

    override fun onTick(tick: TickId, out: OpConsumer): Boolean {
        if (state == 0) {
            game.removeIdentity(avatar.id)
            //todo: add event
            state = 1
            return false
        } else if (state == 1 && tick >= respawnTime) {
            val a = avatar
            out.add(TeleportIn(100500, tick, a.id, to, toX, toY, a.sight))
            state = 2
            return true
        }
        return false
    }
}

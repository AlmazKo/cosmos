package cos.olympus.game.strategy

import cos.olympus.game.Game
import cos.olympus.game.Player
import cos.olympus.game.TickId
import cos.olympus.util.OpConsumer
import cos.ops.out.ProtoAppear
import cos.ops.out.TeleportIn

class TeleportInStrategy(
    private val respawnTime: Int,
    private val t: TeleportIn,
    private val to: Game
) : Strategy {

    override fun onTick(tick: TickId, out: OpConsumer): Boolean {
        if (tick >= respawnTime) {
            val avatar = Player(t.userId, "user:" + t.id)
            val a = to.world.place(avatar, t.x, t.y, 100, 4) //move metrics
            val op = ProtoAppear(1, avatar.id, to.world.name, a.x, a.y, a.sight)
            out.add(op)
            return true
        }
        return false
    }
}

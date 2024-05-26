package cos.olympus.game.strategy

import cos.olympus.game.TickId
import cos.olympus.util.OpConsumer

interface Strategy {
    fun onTick(tick: TickId, out: OpConsumer): Boolean
}

package cos.olympus.game.strategy

import cos.olympus.util.OpConsumer

interface Strategy {
    fun onTick(tick: Int, out: OpConsumer): Boolean
}

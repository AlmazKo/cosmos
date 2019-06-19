package cos.olympus.game.actions

import cos.olympus.Tsm
import cos.olympus.game.Creature

data class Damage(
    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val victim: Creature,
    val culprit: Creature,
    var amount: Int = 0,
    val spellId: Long = 0
) : Action

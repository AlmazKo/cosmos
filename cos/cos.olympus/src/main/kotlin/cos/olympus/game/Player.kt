package cos.olympus.game

import cos.olympus.game.actions.Action
import cos.olympus.game.spells.SpellStrategy
import java.util.*

data class Player(
    override val id: Int,
    override val state: CreatureState
) : Creature {
    val zone = HashMap<Int, Creature>()
    val spellZone = HashMap<Long, SpellStrategy>()

    override val actions = ArrayDeque<Action>()
    override val name get() = "Player_$id"
    override val x get() = state.x
    override val y get() = state.y

    override val viewDistance: Int = 10
}

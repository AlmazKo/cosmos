package cos.olympus.game.strategy

import cos.olympus.game.Creature
import cos.olympus.game.Damages
import cos.olympus.game.events.Spell

interface SpellStrategy {
    //        val action: SpellAction
    val id: Int


    //        fun inZone(creature: Creature): Boolean
    //        fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean
    fun onTick(tick: Int, damages: Damages): Boolean

    fun inZone(cr: Creature): Boolean

    val finished: Boolean

    val spell: Spell
}

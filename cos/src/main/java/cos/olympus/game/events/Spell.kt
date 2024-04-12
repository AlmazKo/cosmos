package cos.olympus.game.events

import cos.olympus.game.Creature

interface Spell {
    val id: Int
    val source: Creature
}

package cos.olympus.game.events

import cos.olympus.game.Actor

interface Spell {
    val id: Int
    val source: Actor
}

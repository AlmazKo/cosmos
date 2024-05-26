package cos.olympus.game.events

import cos.olympus.game.TickId

interface Event {
    val id: Int
    val tick: TickId
}

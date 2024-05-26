package cos.olympus.game

import cos.map.ActorType

interface Agent : Orientable {
    val id: Aid
    val type: ActorType

    fun orientation(): Orientation {
        return Orientation(id, x, y, speed, offset, sight, mv)
    }
}

val Agent.isPlayer: Boolean
    get() = type == ActorType.PLAYER
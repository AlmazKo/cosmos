package cos.olympus.game

import cos.map.ActorType

interface Agent : Orientable {
    val id: Int
    val type: ActorType
}

val Agent.isPlayer: Boolean
    get() = type == ActorType.PLAYER
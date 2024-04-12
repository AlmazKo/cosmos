package cos.olympus.game

import cos.map.CreatureType

interface Agent : Orientable {
    val id: Int
    val type: CreatureType
}

val Agent.isPlayer: Boolean
    get() = type == CreatureType.PLAYER
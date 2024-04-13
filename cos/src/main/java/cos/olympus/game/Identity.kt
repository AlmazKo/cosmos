package cos.olympus.game

import cos.map.ActorType

interface Identity {
    val id: Int
    val type: ActorType
    val name: String
}

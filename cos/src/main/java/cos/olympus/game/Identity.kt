package cos.olympus.game

import cos.map.ActorType

interface Identity {
    val id: Aid
    val type: ActorType
    val name: String
}

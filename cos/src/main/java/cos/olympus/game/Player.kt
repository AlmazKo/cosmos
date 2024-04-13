package cos.olympus.game

import cos.map.ActorType

class Player(
    override val id: Int,
    override val name: String
) : Identity {
    override val type = ActorType.PLAYER
}

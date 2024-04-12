package cos.olympus.game

import cos.map.CreatureType


class Player(
    override val id: Int,
    override val name: String
) : Avatar {
    override val type = CreatureType.PLAYER
}

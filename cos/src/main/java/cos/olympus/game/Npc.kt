package cos.olympus.game

import cos.map.CreatureType

data class Npc(
    override val id: Int,
    override val type: CreatureType,
    override val name: String
) : Avatar

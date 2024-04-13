package cos.olympus.game

import cos.map.ActorType

data class Npc(
    override val id: Int,
    override val type: ActorType,
    override val name: String
) : Identity

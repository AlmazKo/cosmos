package cos.olympus.game

import cos.map.ActorType

data class Npc(
    override val id: Aid,
    override val type: ActorType,
    override val name: String
) : Identity

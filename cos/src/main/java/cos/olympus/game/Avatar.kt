package cos.olympus.game

import cos.map.CreatureType

interface Avatar /* extends Placeable*/ {
    val id: Int
    val type: CreatureType
    val name: String
}

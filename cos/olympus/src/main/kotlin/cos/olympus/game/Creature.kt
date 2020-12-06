//package cos.olympus.game
//
//import cos.olympus.CrId
//import cos.olympus.Inch
//import cos.olympus.Pos
//import cos.olympus.Speed
//import cos.olympus.game.Direction.*
//import cos.olympus.game.Movements.Companion.HALF
//
//data class Creature(
//    override val id: CrId,
//    override val name: String,
//    override var x: Pos,
//    override var y: Pos,
//    override var offset: Inch = HALF,
//    override var speed: Speed = 0,
//    override var dir: Direction = SOUTH,
//    public var sight: Direction = SOUTH
//) : GameObject, VectorObject {
//
//    fun startMove(dir: Direction, sight: Direction, speed: Speed) {
//        this.dir = dir
//        this.sight = sight
//        this.speed = speed
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        return id == (other as Creature).id
//    }
//
//    override fun hashCode() = id
//}

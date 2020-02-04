package cos.olympus.game

import cos.olympus.Pos
import cos.olympus.Speed
import cos.olympus.game.Direction.*

open class Creature(
    override val id: Int,
    override val name: String,
    override var x: Pos,
    override var y: Pos,
    override var offset: Byte = 0,
    override var speed: Speed = 0,
    override var dir: Direction = SOUTH,
    public var sight: Direction = SOUTH
) : GameMapObject, VectorObject {
    //    val viewDistance: Int
    //    val life: Int

    //    val state: CreatureState
    //    val actions: Queue<Action>
    //
    //    fun startStep(step: Step) {
    //        state.direction = step.direction
    //    }
    //
    //    fun set(posX: Int, posY: Int) {
    //        state.x = posX
    //        state.y = posY
    //    }
    //
    //    fun damage(d: Damage) {
    //        state.life -= d.amount
    //        if (state.life < 0) state.life = 0
    //    }
    //
    //    val isDead get() = state.life <= 0

    fun startMove(dir: Direction, sight: Direction, speed: Speed) {
        this.dir = dir
        this.sight = sight
        this.speed = speed
    }
}

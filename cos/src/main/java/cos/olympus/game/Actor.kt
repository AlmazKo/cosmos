package cos.olympus.game

import cos.olympus.game.Movements.Companion.METER
import cos.olympus.game.events.Damage
import cos.olympus.game.events.Death
import cos.olympus.game.strategy.SpellStrategy
import cos.ops.Direction
import cos.ops.Direction.EAST
import cos.ops.Direction.NORTH
import cos.ops.Direction.SOUTH
import cos.ops.Direction.WEST

class Actor(
    val identity: Identity,
    o: Orientation,
    life: Int
) : Agent {

    override var x: Pos = o.x
    override var y: Pos = o.y
    override var offset: Int = o.offset
    override var speed: Int = o.speed
    override var mv: Direction? = o.mv
    override var sight: Direction = o.sight
    override val id get() = identity.id
    override val type get() = identity.type

    var lastSpellTick: Int = 0
    val metrics = Metrics(identity.id, life)
    val bag = Bag()
    val zoneObjects = HashMap<Int, Obj>()
    val zoneActors = HashMap<Aid, Orientation>()
    val zoneMetrics = HashMap<Int, Metrics>()
    val zoneSpells = HashMap<Int, SpellStrategy>()

    fun orientation(): Orientation {
        return Orientation(identity.id, x, y, speed, offset, sight, mv)
    }

    fun copyMetrics(): Metrics {
        return metrics.copy()
    }

    override fun toString(): String {
        return "Actor{" +
            "type=" + type +
            ", id=" + identity.id +
            ", lvl=" + metrics.lvl +
            ", life=" + metrics.life +
            ", pos=[" + rx() + "; " + ry() + "]" +
            ", speed=" + speed +
            ", dir=" + mv +
            ", sight=" + sight +
            '}'
    }

    private fun ry(): Float {
        if (mv == NORTH) return y - (offset.toFloat() / METER)
        if (mv == SOUTH) return y + (offset.toFloat() / METER)
        return y.toFloat()
    }

    private fun rx(): Float {
        if (mv == WEST) return x - (offset.toFloat() / METER)
        if (mv == EAST) return x + (offset.toFloat() / METER)
        return x.toFloat()
    }

    fun stop() {
        offset = 0
        speed = 0
        mv = null
    }

    fun damage(d: Damage) {
        metrics.minus(d.amount)
    }

    val isDead: Boolean
        get() = metrics.isDead

    val life: Int = metrics.life()

    fun onKill(death: Death) {
        if (death.victim.metrics.lvl > metrics.lvl) {
            metrics.exp += 3
        } else if (death.victim.metrics.lvl >= metrics.lvl - 1) {
            metrics.exp += 1
        }

        if (metrics.exp >= 10) {
            metrics.lvl++
            metrics.exp = metrics.exp - 10
            metrics.maxLife = (metrics.maxLife + 1.2).toInt()
            metrics.life = metrics.maxLife
            println("$this level up")
        }
    }
}

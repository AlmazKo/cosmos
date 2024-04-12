package cos.olympus.game

import cos.olympus.game.events.Damage
import cos.olympus.game.events.Death
import cos.olympus.game.strategy.SpellStrategy
import cos.ops.Direction

class Creature(
    val avatar: Avatar,
    override var x: Int,
    override var y: Int,
    override var offset: Int,
    override var speed: Int,
    override var mv: Direction?,
    override var sight: Direction,
    life: Int
) : Agent {

    override val id get() = avatar.id
    override val type get() = avatar.type

    var lastSpellTick: Int = 0
    val metrics = Metrics(avatar.id, life)
    val bag = Bag()
    val zoneObjects = HashMap<Int, Obj>()
    val zoneCreatures = HashMap<Int, Orientation>()
    val zoneMetrics = HashMap<Int, Metrics>()
    val zoneSpells = HashMap<Int, SpellStrategy>()

    fun orientation(): Orientation {
        return Orientation(avatar.id, x, y, speed, offset, sight, mv)
    }

    fun copyMetrics(): Metrics {
        return metrics.copy()
    }


    override fun toString(): String {
        return "Creature{" +
            "id=" + avatar.id +
            ", lvl=" + metrics.lvl +
            ", life=" + metrics.life +
            ", type=" + type +
            ", pos=[" + rx() + "; " + ry() + "]" +
            ", speed=" + speed +
            ", dir=" + mv +
            ", sight=" + sight +
            '}'
    }

    fun ry(): Float {
        if (mv == Direction.NORTH) return y - (offset.toFloat() / Movements.METER)
        if (mv == Direction.SOUTH) return y + (offset.toFloat() / Movements.METER)
        return y.toFloat()
    }

    fun rx(): Float {
        if (mv == Direction.WEST) return x - (offset.toFloat() / Movements.METER)
        if (mv == Direction.EAST) return x + (offset.toFloat() / Movements.METER)
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

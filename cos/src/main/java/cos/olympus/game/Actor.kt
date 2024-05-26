package cos.olympus.game

import cos.olympus.game.events.Damage
import cos.olympus.game.events.Death
import cos.olympus.game.strategy.SpellStrategy
import cos.ops.Direction

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

    var lastSpellTick: TickId = 0
    val metrics = Metrics(identity.id, life)
    val bag = Bag()
    val zoneObjects = HashMap<Int, Obj>()
    val zoneActors = HashMap<Aid, Orientation>()
    val zoneMetrics = HashMap<Int, Metrics>()
    val zoneSpells = HashMap<Int, SpellStrategy>()

    fun inZone(obj: Obj): Boolean = zoneObjects.contains(obj.id)

    fun addInZone(obj: Obj) {
        zoneObjects[obj.id] = obj
    }

    fun addInZone(actor: Actor) {
        zoneActors[actor.id] = actor.orientation()
    }

    fun addMetricsInZone(actor: Actor) {
        zoneMetrics[actor.id] = actor.metrics.copy()
    }

    val isDead: Boolean get() = metrics.isDead
    val life: Int = metrics.life

    fun stop() {
        offset = 0
        speed = 0
        mv = null
    }

    fun damage(d: Damage) {
        metrics.minus(d.amount)
    }

    fun onKill(death: Death) {
        if (death.victim.metrics.lvl > metrics.lvl) {
            metrics.exp += 3
        } else if (death.victim.metrics.lvl >= metrics.lvl - 1) {
            metrics.exp += 1
        }

        if (metrics.exp >= 10) {
            metrics.lvl++
            //todo levelup event
            metrics.exp = metrics.exp - 10
            metrics.maxLife = (metrics.maxLife + 1.2).toInt()
            metrics.life = metrics.maxLife
        }
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
}

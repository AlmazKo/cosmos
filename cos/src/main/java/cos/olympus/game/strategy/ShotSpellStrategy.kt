package cos.olympus.game.strategy

import cos.logging.Logger
import cos.olympus.Util
import cos.olympus.game.Actor
import cos.olympus.game.Damages
import cos.olympus.game.MapUtil.inZone
import cos.olympus.game.World
import cos.olympus.game.events.Shot
import cos.ops.Direction

class ShotSpellStrategy(
    override val spell: Shot,
    private val world: World
) : SpellStrategy {


    private var passed = 0
    var x = spell.x
    var y = spell.y

    override val id = spell.id
    override var finished = false

    override fun onTick(tick: Int, damages: Damages): Boolean {
        val distance = (tick - spell.tick) * spell.speed / 100

        x = spell.x
        y = spell.y

        when (spell.dir) {
            Direction.NORTH -> y -= distance
            Direction.EAST -> x += distance
            Direction.SOUTH -> y += distance
            Direction.WEST -> x -= distance
        }
        val victim = world.getActor(x, y)
        if (victim != null && spell.source.id != victim.id) {
            val crit = Util.rand(0, 5) == 1
            damages.on(victim, spell, if (crit) 200 else 100, crit)
            finished = true
        }
        if (distance >= spell.distance) {
            finished = true
        }

        if (distance > passed) {
            passed = distance
        }

        //LOG.info("Spell distance: $this")
        return finished
    }

    override fun inZone(actor: Actor): Boolean {
        return inZone(actor, x, y, 8)
    }


    override fun toString(): String {
        return "ShotSpellStrategy{" +
            "passed=" + passed +
            ", x=" + x +
            ", y=" + y +
            '}'
    }

    companion object {
        val LOG = Logger.get(ShotSpellStrategy::class.java)
    }
}


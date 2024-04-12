package cos.olympus.game.strategy

import cos.olympus.Util
import cos.olympus.game.Creature
import cos.olympus.game.Damages
import cos.olympus.game.MapUtil.inZone
import cos.olympus.game.World
import cos.olympus.game.events.Fireball
import cos.ops.Direction

class FireballSpellStrategy(
    override val spell: Fireball,
    private val world: World
) : SpellStrategy {

    override var finished = false
    private var passed = 0
    var x = spell.x
    var y = spell.y

    override val id = spell.id

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
        val victim = world.getCreature(x, y)
        if (victim != null && spell.source.id != victim.id) {
            val crit = Util.rand(0, 10) == 1
            damages.on(victim, spell, if (crit) 100 else 50, crit)
            finished = true
        }
        if (distance >= spell.distance) {
            finished = true
        }

        if (distance > passed) {
            passed = distance
        }

        //logger.info("Spell distance: " + this);
        return finished
    }

    override fun inZone(cr: Creature): Boolean {
        return inZone(cr, x, y, 8)
    }

    override fun toString() = "FireballSpellStrategy{passed=$passed, x=$x, y=$y}"
}


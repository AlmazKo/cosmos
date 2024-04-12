package cos.olympus.game

import cos.logging.Logger
import cos.olympus.game.events.Damage
import cos.olympus.game.events.Spell
import java.util.function.Consumer

class Damages : TickAware {
    private var data = ArrayList<Damage>()
    private var tick = 0

    override fun onTick(tick: Int) {
        this.tick = tick
    }

    fun on(victim: Creature, spell: Spell, amount: Int, crit: Boolean) {
        val dmg = Damage(++DAMAGES_IDS, tick, victim, spell, amount, crit)
        ///        logger.info(dmg);
        data.add(dmg)
    }

    fun forEach(consumer: Consumer<Damage>?) {
        if (data.isEmpty()) return

        data.forEach(consumer)
    }

    fun clear() {
        if (data.isEmpty()) return

        data = ArrayList()
    }

    companion object {
        private var DAMAGES_IDS = 0
    }
}

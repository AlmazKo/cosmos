package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Spell;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Damages implements TickAware {
    private static int DAMAGES_IDS = 0;
    private final static Logger logger = Logger.get(Damages.class);
    private ArrayList<Damage> data = new ArrayList<>();
    private int tick;

    @Override
    public void onTick(int tick) {
        this.tick = tick;
    }

    public void on(Creature victim, Spell spell, int amount, boolean crit) {
        var dmg = new Damage(++DAMAGES_IDS, tick, victim, spell, amount, crit);
        logger.info(dmg);
        data.add(dmg);
    }

    public void forEach(Consumer<Damage> consumer) {
        if (data.isEmpty()) return;

        data.forEach(consumer);
    }

    public void clear() {
        if (data.isEmpty()) return;

        data = new ArrayList<>();
    }
}

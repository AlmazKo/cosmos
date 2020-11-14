package cos.olympus.game;

import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Spell;
import cos.ops.OutOp;

import java.util.Collection;

public interface SpellStrategy {
    //        val action: SpellAction
    int id();
//        fun inZone(creature: Creature): Boolean
//        fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean


    boolean onTick(int tick, Collection<OutOp> consumer, Collection<Damage> damages);

    boolean inZone(Creature cr);
    boolean isFinish();
    Spell spell();
}

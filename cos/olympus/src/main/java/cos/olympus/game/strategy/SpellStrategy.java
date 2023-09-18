package cos.olympus.game.strategy;

import cos.olympus.game.Creature;
import cos.olympus.game.Damages;
import cos.olympus.game.events.Spell;

public interface SpellStrategy {
    //        val action: SpellAction
    int id();
//        fun inZone(creature: Creature): Boolean
//        fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean


    boolean onTick(int tick, Damages damages);

    boolean inZone(Creature cr);

    boolean isFinished();

    Spell spell();
}

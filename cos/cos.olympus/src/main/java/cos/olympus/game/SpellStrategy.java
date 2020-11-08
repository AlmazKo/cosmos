package cos.olympus.game;

import cos.ops.OutOp;

import java.util.Collection;

public interface SpellStrategy {

    //        val action: SpellAction
    int id();
//        fun inZone(creature: Creature): Boolean
//        fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean


    boolean onTick(int tick, Collection<OutOp> consumer);

    boolean inZone(Creature cr);
}

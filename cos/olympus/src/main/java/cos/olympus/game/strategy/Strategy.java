package cos.olympus.game.strategy;

import cos.olympus.util.OpConsumer;

public interface Strategy {
    boolean onTick(int tick, OpConsumer out);
}

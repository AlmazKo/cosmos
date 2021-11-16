package cos.olympus;

import cos.olympus.util.OpConsumer;

public interface Strategy {
    boolean onTick(int tick, OpConsumer outOps);
}

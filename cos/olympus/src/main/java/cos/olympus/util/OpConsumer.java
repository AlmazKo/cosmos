package cos.olympus.util;

import cos.ops.OutOp;

@FunctionalInterface
public interface OpConsumer {
    void add(OutOp op);
}

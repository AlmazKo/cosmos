package cos.olympus.util;

import cos.ops.OutOp;
import cos.ops.SomeOp;

@FunctionalInterface
public interface OpConsumer {
    void add(SomeOp op);
}

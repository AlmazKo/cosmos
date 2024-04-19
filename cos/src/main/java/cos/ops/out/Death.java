package cos.ops.out;

import cos.ops.OutOp;

public record Death(
        @Override int id,
        @Override int userId,
        int sourceId,
        int victimId

) implements OutOp {


}

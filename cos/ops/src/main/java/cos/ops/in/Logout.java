package cos.ops.in;

import cos.ops.InOp;

public record Logout(
        @Override int id,
        @Override int userId
) implements InOp {

}

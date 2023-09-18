package cos.ops.out;

import java.util.Arrays;


public record UserPackage(
        int tick,
        int userId,
        Record[] ops
) {


    @Override
    public String toString() {
        return "UserPackage{" +
                "tick=" + tick +
                ", userId=" + userId +
                ", ops=" + Arrays.deepToString(ops) +
                '}';
    }
}

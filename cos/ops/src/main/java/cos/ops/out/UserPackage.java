package cos.ops.out;

import java.util.Arrays;


public record UserPackage(
        int tick,
        long tickTimeMs,
        int userId,
        Record[] ops
) {


    @Override
    public String toString() {
        return "UserPackage{" +
                "tick=" + tick +
                "tickTimeMs=" + tickTimeMs +
                ", userId=" + userId +
                ", ops=" + Arrays.deepToString(ops) +
                '}';
    }
}

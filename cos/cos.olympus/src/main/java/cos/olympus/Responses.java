package cos.olympus;

import cos.ops.OutOp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Responses {
    public volatile ArrayList<OutOp> ops = new ArrayList<>();

    public synchronized void flush(ByteBuffer buf) {
        for (OutOp op : ops) {
            op.write(buf);
        }

        ops.clear();
    }
}

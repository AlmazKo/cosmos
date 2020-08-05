package cos.olympus;

import cos.ops.OutOp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Responses {
    public volatile ArrayList<OutOp> ops = new ArrayList<>();

    public synchronized ByteBuffer flush() {
        var buf = ByteBuffer.allocate(1024);

        for (OutOp op : ops) {
            op.write(buf);
        }

        ops.clear();
//        buf.rewind();
        return buf;
    }
}

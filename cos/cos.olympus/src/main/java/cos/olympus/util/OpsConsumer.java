package cos.olympus.util;

import cos.ops.OutOp;

import java.util.ArrayList;

public class OpsConsumer implements OpConsumer {

    public final ArrayList<OutOp> data = new ArrayList<>();

    @Override
    public void add(OutOp op) {
        data.add(op);
    }

    public int size() {
        return data.size();
    }
}

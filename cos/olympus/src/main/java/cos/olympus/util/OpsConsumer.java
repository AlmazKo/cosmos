package cos.olympus.util;

import cos.ops.OutOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpsConsumer implements OpConsumer {

    public final HashMap<Integer, ArrayList<OutOp>> data = new HashMap<>();

    @Override
    public void add(OutOp op) {
        data.computeIfAbsent(op.userId(), u -> new ArrayList<>()).add(op);
    }

    public int size() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }


    public List<OutOp> getUserData(int userId) {
        var result = data.get(userId);
        if (result == null) return List.of();

        return result;
    }
}

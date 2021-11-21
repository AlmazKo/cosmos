package cos.olympus.util;

import cos.ops.OutOp;
import cos.ops.SomeOp;
import cos.ops.UserOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpsConsumer implements OpConsumer {

    public final HashMap<Integer, ArrayList<SomeOp>> data = new HashMap<>();

    @Override
    public void add(SomeOp op) {
        if (op instanceof UserOp uop) {
            data.computeIfAbsent(uop.userId(), u -> new ArrayList<>()).add(uop);
        } else  {
            data.computeIfAbsent(0, u -> new ArrayList<>()).add(op);
        }
    }

    public int size() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }


    public List<SomeOp> getUserData(int userId) {
        var result = data.get(userId);
        if (result == null) return List.of();

        return result;
    }
}

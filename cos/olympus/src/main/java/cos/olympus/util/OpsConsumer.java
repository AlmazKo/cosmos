package cos.olympus.util;

import cos.logging.Logger;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.out.AllCreatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpsConsumer implements OpConsumer {

    private final static Logger LOG = Logger.get(OpsConsumer.class);

    public final HashMap<Integer, List<SomeOp>> data = new HashMap<>();

    @Override
    public void add(SomeOp op) {
        if (op instanceof UserOp uop) {
          ////  LOG.info("New user op: " + op);
            data.computeIfAbsent(uop.userId(), u -> new ArrayList<>()).add(uop);
        } else if (op instanceof AllCreatures) {
            data.computeIfAbsent(0, u -> new ArrayList<>()).add(op);
        } else {
            LOG.info("New service op: " + op);
            data.computeIfAbsent(0, u -> new ArrayList<>()).add(op);
        }
    }

    public int size() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }


    public List<SomeOp> getServiceData() {
        return data.getOrDefault(0, List.of());
    }

    public List<SomeOp> getUserData(int userId) {
        if (userId == 0) throw new IllegalArgumentException("UserId must be greater than 0");

        var result = data.get(userId);
        if (result == null) return List.of();

        return result;
    }
}

package cos.olympus.util;

import cos.logging.Logger;
import cos.ops.ServiceOp;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.out.UserPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpsAggregator implements OpConsumer {

    private final static Logger LOG = Logger.get(OpsAggregator.class);

    private final HashMap<Integer, List<SomeOp>> data = new HashMap<>();
    private List<ServiceOp> serviceOps = new ArrayList<>();

    @Override
    public void add(SomeOp op) {
//        LOG.info(op, "new_op");

        if (op instanceof ServiceOp o) {
            serviceOps.add(o);
            return;
        }

        int usrId = switch (op) {
            case UserOp uop -> uop.userId();
            default -> 0;
        };

        data.computeIfAbsent(usrId, it -> new ArrayList<>())
                .add(op);
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

    public Map<Integer, List<SomeOp>> userOps() {
        return data;
    }

    public List<ServiceOp> serviceOps() {
        if (serviceOps.isEmpty()) return List.of();

        var tmp = serviceOps;
        serviceOps = new ArrayList<>();
        return tmp;
    }

    public List<SomeOp> adminOps() {
        List<SomeOp> tmp = data.get(0);
        return (tmp == null) ? List.of() : tmp;
    }

    public List<SomeOp> getUserData(int userId) {
        if (userId == 0) throw new IllegalArgumentException("UserId must be greater than 0");

        var result = data.get(userId);
        if (result == null) return List.of();

        return result;
    }


    public ArrayList<UserPackage> groupByUser(int tick) {
        var out = new ArrayList<UserPackage>();
        data.forEach((userId, ops) -> {
            if (userId > 0 && userId < 10000) {
                var op = new UserPackage(tick, userId, ops.toArray(new Record[0]));
                out.add(op);
            }
        });
        return out;
    }
}

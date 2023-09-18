package cos.olympus.game;

import cos.olympus.util.OpsConsumer;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.out.ProtoAppear;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static cos.olympus.GameUtil.prepareGame;

class TestGame {
    private int tick = 0;
    private MetaGame game;
    private OpsConsumer out = new OpsConsumer();
    private List<SomeOp> serviceOps = List.of();

    TestGame() {
        try {
            game = prepareGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void tick(UserOp... ops) {
        ++tick;
        out.clear();
        game.onTick(tick, Arrays.asList(ops), serviceOps, out);
        serviceOps = out.getServiceData();
    }

    @NotNull ProtoAppear appear(int userId) {
        var a = out.getUserData(userId).stream().filter(ProtoAppear.class::isInstance).map(ProtoAppear.class::cast).findFirst().get();
        return a;
    }

    void many(int times) {
        for (int i = 0; i < times; i++) {
            tick();
        }
    }


}

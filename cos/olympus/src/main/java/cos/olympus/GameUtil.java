package cos.olympus;

import cos.map.Land;
import cos.map.Lands;
import cos.olympus.game.Game;
import cos.olympus.game.MetaGame;
import cos.olympus.game.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class GameUtil {

    public static @NotNull Lands parseResources(String name) throws IOException {
        var dir = System.getProperty("CosResourcesDir");
        var res = (dir == null || dir.isBlank()) ? Paths.get("", "../../resources") : Paths.get("", dir);
        return Land.load(res.toAbsolutePath(), name);
    }

    public static @NotNull MetaGame prepareGame() throws IOException {
        var lands = parseResources("map");
        var lands2 = parseResources("map_mike");
        var games = Map.of(
                "map", new Game(new World(lands, "map")),
                "map_mike", new Game(new World(lands2, "map_mike"))
        );

        return new MetaGame(games);
    }

}

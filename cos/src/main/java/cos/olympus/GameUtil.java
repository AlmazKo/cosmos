package cos.olympus;

import cos.Properties;
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
        return Land.load(Properties.resourcesDir, name);
    }

    public static @NotNull MetaGame prepareGame() throws IOException {
        var lands = parseResources("castle-island");
        var lands2 = parseResources("map_mike");
        var games = Map.of(
                "castle-island", new Game(new World(lands, "castle-island")),
                "map_mike", new Game(new World(lands2, "map_mike"))
        );

        return new MetaGame(games);
    }

}

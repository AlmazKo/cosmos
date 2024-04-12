package cos.olympus.game

import cos.Properties
import cos.map.Land
import cos.map.Lands
import java.util.Map

object GameUtil {

    fun parseResources(name: String?): Lands {
        return Land.load(Properties.resourcesDir, name)
    }

    @JvmStatic
    fun prepareGame(): MetaGame {
        val lands = parseResources("castle-island")
        val lands2 = parseResources("map_mike")
        val games = Map.of(
            "castle-island", Game(World(lands, "castle-island")),
            "map_mike", Game(World(lands2, "map_mike"))
        )

        return MetaGame(games)
    }
}

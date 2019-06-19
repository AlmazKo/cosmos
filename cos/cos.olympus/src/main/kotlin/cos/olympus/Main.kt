package cos.olympus

import com.google.common.flogger.FluentLogger
import cos.map.Coord
import cos.map.Land
import cos.olympus.game.Game
import cos.olympus.game.GameMap

import java.util.logging.Level.INFO

object Main {

    private val logger = FluentLogger.forEnclosingClass()

    @Throws(InterruptedException::class)
    @JvmStatic fun main(args: Array<String>) {

        val lands = Land.load()
        val (x1, y) = Coord(1, 2)
        val gameMap = GameMap(lands)
        val game = Game(gameMap)
        var x = 0
        while (true) {
            logger.at(INFO).log("%d", x++)
            Thread.sleep(250)
        }

    }
}

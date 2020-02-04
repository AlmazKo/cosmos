package cos.olympus

import com.google.common.flogger.FluentLogger
import cos.map.Coord
import cos.map.Land
import cos.olympus.game.DoubleBuffer
import cos.olympus.game.Game
import cos.olympus.game.GameMap
import cos.olympus.game.GameServer
import java.lang.System.nanoTime

object Main {

    private val logger = FluentLogger.forEnclosingClass()

    @Throws(InterruptedException::class)
    @JvmStatic fun main(args: Array<String>) {

        val actionsBuffer = DoubleBuffer()
        val lands = Land.load()
        val (x1, y) = Coord(1, 2)
        val gameMap = GameMap(lands)
        val game = Game(gameMap)
        GameServer.run(actionsBuffer)
        var x = 0

        var id = 0;
        while (true) {
            val start = nanoTime()
            game.onTick(++id, tsm())
            val execTime = nanoTime() - start
//            logger.at(INFO).log("%.3fms", execTime / 1000000.0)
            Thread.sleep(500)

        }

    }
}

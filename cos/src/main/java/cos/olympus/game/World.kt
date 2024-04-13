package cos.olympus.game

import cos.logging.Logger
import cos.map.Coord
import cos.map.Lands
import cos.map.PortalSpot
import cos.map.RespawnSpot
import cos.map.Tile
import cos.map.TileType
import cos.olympus.NoSpaceException
import cos.olympus.game.MapUtil.nextX
import cos.olympus.game.MapUtil.nextY
import cos.olympus.util.XYConsumer
import cos.ops.Direction
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min


class World(lands: Lands, val name: String) {
    val width = lands.width
    val height = lands.height
    private val basis: ShortArray = lands.basis
    private val objects: ShortArray = lands.objects
    private val tiles: Array<Tile?> = lands.tiles
    private val actorsXY = IntArray(basis.size)

    private val actors = HashMap<Aid, Actor>()

    val offsetX = lands.offsetX
    val offsetY = lands.offsetY
    val respawns: ArrayList<RespawnSpot> = lands.respawns
    val portals: ArrayList<PortalSpot> = lands.portals

    init {
        //        debug();
    }

    fun debug() {
        val sb = StringBuilder()
        for (i in basis.indices) {
            val it = basis[i].toInt()
            if (i % width == 0) {
                sb.append('\n')
                sb.append(String.format("%1$-4s", i / width + offsetY))
            }

            if (it == 0) {
                sb.append('.')
            } else {
                val tile = tiles[it]
                if (tile == null) {
                    sb.append('?')
                    continue
                }

                val b = when (tile.type) {
                    TileType.WALL -> '#'
                    TileType.TIMBER -> 'T'
                    TileType.SHALLOW -> '~'
                    TileType.DEEP_WATER -> '≈'
                    TileType.GRASS -> 'v'
                    TileType.SAND -> '.'
                    TileType.GATE -> 'П'
                    TileType.NOTHING -> 'x'
                    else -> 'N'
                }

                sb.append(b)
            }
        }

        println(sb)
    }

    operator fun get(x: Pos, y: Pos): TileType? {
        val idx = toIndex(x, y)
        if (idx < 0 || idx >= basis.size) return null
        val b = basis[idx]
        val t = tiles[b.toInt()]
        return if ((t == null)) null else t.type
    }

    fun getObject(x: Pos, y: Pos): Obj? {
        val idx = toIndex(x, y)
        if (idx < 0 || idx >= objects.size) return null

        val objTileId = objects[idx]
        if (objTileId.toInt() == 0) return null
        if (objTileId >= tiles.size) return null

        val t = tiles[objTileId.toInt()]
            ?: return Obj(idx, Tile(objTileId.toInt(), TileType.ITEM), x, y)

        return Obj(idx, t, x, y) //todo id is hardcoded
    }

    fun getActor(uid: Aid): Actor? {
        return actors[uid]
    }

    fun getActor(x: Pos, y: Pos): Actor? {
        if (!isValid(x, y)) return null

        return _getActor(x, y)
    }

    private fun _getActor(x: Pos, y: Pos): Actor? {
        val crId = actorsXY[toIndex(x, y)]
        return actors[crId]
    }

    fun getActors(centerX: Pos, centerY: Pos, radius: Int): List<Actor> {
        val result = ArrayList<Actor>()
        val minX = min(centerX + radius, width + offsetX)
        val maxX = max(offsetX, centerX - radius)
        val maxY = max(offsetY, centerY - radius)
        val minY = min(centerY + radius, height + offsetY)

        for (x in maxX..minX) {
            for (y in maxY..minY) {
                if (x == centerX && y == centerY) continue

                val actor = _getActor(x, y)
                if (actor != null) {
                    result.add(actor)
                }
            }
        }

        return result
    }

    fun iterateAround(centerX: Pos, centerY: Pos, radius: Int, consumer: XYConsumer) {
        for (x in max(offsetX, (centerX - radius))..min((centerX + radius), (width + offsetX))) {
            for (y in max(offsetY, (centerY - radius))..min((centerY + radius), (height + offsetY))) {
//                if (x == centerX && y == centerY) continue;
                consumer.accept(x, y)
            }
        }
    }

    fun isNoActor(id: Aid): Boolean {
        return !actors.containsKey(id)
    }

    fun removeActor(id: Aid) {
        val a = actors.remove(id)
        if (a == null) {
            logger.warn("$name: Not found actor$id for removing")
            return
        }

        val idx = toIndex(a.x, a.y)

        //todo debug
        if (actorsXY[idx] != id) {
            throw RuntimeException("Wrong position #$id")
        }
        actorsXY[idx] = 0
    }

    fun removeActorIf(filter: Predicate<in Actor>) {
        actors.values.removeIf { a: Actor ->
            if (filter.test(a)) {
                val idx = toIndex(a.x, a.y)
                actorsXY[idx] = 0
                true
            } else {
                false
            }
        }
    }

    fun place(usr: Identity?, x: Pos, y: Pos, life: Int, maxDev: Int): Actor {
        var idx = toIndex(x, y)
        if (idx < 0 || idx >= basis.size) {
            throw NoSpaceException("Fail finding free place")
        }

        idx = findFreeIndex(x, y, maxDev)

        if (idx >= 0) {
            val coord = toCoord(idx)
            val orient = Orientation(0, coord.x, coord.y, 0, 0, Direction.SOUTH, null)
            val a = Actor(usr!!, orient, life)
            actorsXY[idx] = a.id
            actors[a.id] = a
            logger.info(a, "placed")
            return a
        } else {
            throw NoSpaceException("Fail finding free place")
        }
    }

    fun hasActor(x: Pos, y: Pos): Boolean {
        return !isNoActor(x, y)
    }

    fun isNoActor(x: Pos, y: Pos): Boolean {
        if (!isValid(x, y)) return false

        return actorsXY[toIndex(x, y)] == 0
    }

    fun isNoMovingActorIn(x: Pos, y: Pos): Boolean {
        val crs = getActors(x, y, 1)
        for (o in crs) {
            if (o.speed > 0 && (nextX(o) == x && nextY(o) == y)) {
                return false
            }
        }

        return true
    }

    fun isFree(x: Pos, y: Pos): Boolean {
        if (!isValid(x, y)) return false
        val idx = toIndex(x, y)

        var b = get(x, y)
        if (b == null || b == TileType.WALL || b == TileType.DEEP_WATER || b == TileType.NOTHING) return false
        val o = getObject(x, y)
        if (o != null) {
            b = o.tile.type
            if (b == null || b == TileType.WALL || b == TileType.DEEP_WATER || b == TileType.NOTHING) return false
        }

        return actorsXY[idx] == 0
    }

    fun move(a: Actor, toX: Pos, toY: Pos) {
        val from = toIndex(a.x, a.y)
        val to = toIndex(toX, toY)
        val actorId = actorsXY[from]

        if (actorId == 0) {
            logger.warn("$name: Try moving from free place $toX, $toY")
        }
        if (actorsXY[to] != 0) {
            logger.warn("$name: Try moving into occupied place $toX, $toY")
        }

        actorsXY[from] = 0
        actorsXY[to] = actorId
        a.x = toX
        a.y = toY

        ////        logger.info(name + ": Actor #" + actorId + " set x=" + toX + ", y=" + toY);
    }

    fun findFreePlace(x: Pos, y: Pos, maxDev: Int): Coord? {
        val idx = findFreeIndex(x, y, maxDev)
        return if (idx == -1) {
            null
        } else {
            toCoord(idx)
        }
    }

    /*
    Search
     xxxxxxx
     x ┏━━┓x
     x ┃╳┓┃x
     x ┗━┛┃x
     x ╍╍━┛x
     */
    private fun findFreeIndex(x: Pos, y: Pos, maxDev: Int): Int {
        var x = x
        var y = y
        if (!isValid(x, y)) return -1

        if (actorsXY[toIndex(x, y)] == 0) return toIndex(x, y)

        for (i in 1..maxDev) {
            if (i % 2 == 1) {
                for (s in 0 until i) {
                    x++
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y)
                }

                for (s in 0 until i) {
                    y++
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y)
                }
            } else {
                for (s in 0 until i) {
                    x--
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y)
                }

                for (s in 0 until i) {
                    y--
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y)
                }
            }
        }

        return -1
    }

    fun debugActors(): String {
        val sb = StringBuilder()
        for (i in actorsXY.indices) {
            val it = actorsXY[i]
            if (i % width == 0) {
                sb.append('\n')
                sb.append(String.format("%1$-4s", i / width + offsetY))
            }

            if (it == 0) {
                sb.append('.')
            } else if (it >= 1000) {
                sb.append('c')
            } else {
                sb.append('p')
            }
        }

        return sb.toString()
    }

    //    @Contract(pure = true)
    private fun isValid(x: Pos, y: Pos): Boolean {
        return (x >= offsetX && x < offsetX + width && y >= offsetY) && y < (offsetY + height)
    }

    private fun toIndex(x: Pos, y: Pos): Int {
        return x - offsetX + (y - offsetY) * width
    }

    private fun toCoord(idx: Pos): Coord {
        return Coord(idx % width + offsetX, idx / width + offsetY)
    }

    override fun toString(): String {
        return debugActors()
    }

    val allActors: Collection<Actor>
        get() = actors.values

    val allPlayers: Collection<Actor>
        get() = actors.values

    companion object {
        private val logger: Logger = Logger.get(World::class.java)
    }
}

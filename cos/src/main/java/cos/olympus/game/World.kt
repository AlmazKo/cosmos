package cos.olympus.game;


import cos.logging.Logger;
import cos.map.Coord;
import cos.map.Lands;
import cos.map.PortalSpot;
import cos.map.RespawnSpot;
import cos.map.Tile;
import cos.map.TileType;
import cos.olympus.NoSpaceException;
import cos.olympus.util.XYConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static cos.ops.Direction.SOUTH;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class World {
    private final static Logger logger = Logger.get(World.class);
    final int width;
    final int height;
    private final short[] basis;
    private final short[] objects;
    private final String name;
    private final Tile[] tiles;
    private final int[] actorsXY;

    private final HashMap<Integer, Actor> actors = new HashMap<>();

    final int offsetX;
    final int offsetY;
    final ArrayList<RespawnSpot> respawns;
    final ArrayList<PortalSpot> portals;

    public World(Lands lands, String name) {
        this.offsetX = lands.offsetX();
        this.offsetY = lands.offsetY();
        this.width = lands.width();
        this.height = lands.height();
        this.basis = lands.basis();
        this.objects = lands.objects();
        this.name = name;
        this.actorsXY = new int[basis.length];
        this.tiles = lands.tiles();
        this.respawns = lands.respawns();
        this.portals = lands.portals();
//        debug();
    }

    public String getName() {
        return name;
    }

    public void debug() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < basis.length; i++) {

            short it = basis[i];
            if (i % width == 0) {
                sb.append('\n');
                sb.append(String.format("%1$-4s", i / width + offsetY));
            }

            if (it == 0) {
                sb.append('.');
            } else {
                Tile tile = tiles[it];
                if (tile == null) {
                    sb.append('?');
                    continue;
                }

                char b = switch (tile.type()) {
                    case WALL -> '#';
                    case TIMBER -> 'T';
                    case SHALLOW -> '~';
                    case DEEP_WATER -> '≈';
                    case GRASS -> 'v';
                    case SAND -> '.';
                    case GATE -> 'П';
                    case NOTHING -> 'x';
                    default -> 'N';
                };

                sb.append(b);
            }

        }

        System.out.println(sb);
    }

    @Nullable
    public TileType get(int x, int y) {
        int idx = toIndex(x, y);
        if (idx < 0 || idx >= basis.length) return null;
        var b = basis[idx];
        var t = tiles[b];
        return (t == null) ? null : t.type();
    }

    public @Nullable Obj getObject(int x, int y) {
        int idx = toIndex(x, y);
        if (idx < 0 || idx >= objects.length) return null;

        var objTileId = objects[idx];
        if (objTileId == 0) return null;
        if (objTileId >= tiles.length) return null;

        var t = tiles[objTileId];
        if (t == null) return new Obj(idx, new Tile(objTileId, TileType.ITEM), x, y);

        return new Obj(idx, t, x, y);//todo id is hardcoded
    }

    public @Nullable Actor getActor(int uid) {
        return actors.get(uid);
    }

    public @Nullable Actor getActor(int x, int y) {
        if (!isValid(x, y)) return null;

        return _getActor(x, y);
    }

    @Nullable
    private Actor _getActor(int x, int y) {
        int crId = actorsXY[toIndex(x, y)];
        return actors.get(crId);
    }

    public List<@NotNull Actor> getActors(int centerX, int centerY, int radius) {

        ArrayList<@NotNull Actor> result = new ArrayList<>();

        for (int x = max(offsetX, centerX - radius); x <= min(centerX + radius, width + offsetX); x++) {
            for (int y = max(offsetY, centerY - radius); y <= min(centerY + radius, height + offsetY); y++) {
                if (x == centerX && y == centerY) continue;

                @Nullable Actor actor = _getActor(x, y);
                if (actor != null) {
                    result.add(actor);
                }
            }
        }

        return result;
    }

    public void iterateAround(int centerX, int centerY, int radius, XYConsumer consumer) {
        for (int x = max(offsetX, centerX - radius); x <= min(centerX + radius, width + offsetX); x++) {
            for (int y = max(offsetY, centerY - radius); y <= min(centerY + radius, height + offsetY); y++) {
//                if (x == centerX && y == centerY) continue;
                consumer.accept(x, y);
            }
        }

    }

    public boolean isNoActor(int id) {
        return !actors.containsKey(id);
    }

    void removeActor(int id) {
        var a = actors.remove(id);
        if (a == null) {
            logger.warn(name + ": Not found actor" + id + " for removing");
            return;
        }

        int idx = toIndex(a.getX(), a.getY());

        //todo debug
        if (actorsXY[idx] != id) {
            throw new RuntimeException("Wrong position #" + id);
        }
        actorsXY[idx] = 0;
    }

    public void removeActorIf(Predicate<? super Actor> filter) {
        actors.values().removeIf(a -> {
            if (filter.test(a)) {
                int idx = toIndex(a.getX(), a.getY());
                actorsXY[idx] = 0;
                return true;
            } else {
                return false;
            }
        });
    }

    public Actor place(Identity usr, int x, int y, int life, int maxDev) {

        int idx = toIndex(x, y);
        if (idx < 0 || idx >= basis.length) {
            throw new NoSpaceException("Fail finding free place");
        }

        idx = findFreeIndex(x, y, maxDev);

        if (idx >= 0) {
            var coord = toCoord(idx);
            var orient = new Orientation(0, coord.x(), coord.y(), 0, 0, SOUTH, null);
            var a = new Actor(usr, orient, life);
            actorsXY[idx] = a.getId();
            actors.put(a.getId(), a);
            logger.info(a, "placed");
            return a;
        } else {
            throw new NoSpaceException("Fail finding free place");
        }
    }

    public boolean hasActor(int x, int y) {
        return !isNoActor(x, y);
    }

    public boolean isNoActor(int x, int y) {
        if (!isValid(x, y)) return false;

        return actorsXY[toIndex(x, y)] == 0;
    }

    public boolean isNoMovingActorIn(int x, int y) {
        var crs = getActors(x, y, 1);
        for (Orientable o : crs) {
            if (o.getSpeed() > 0 && (MapUtil.INSTANCE.nextX(o) == x && MapUtil.INSTANCE.nextY(o) == y)) {
                return false;
            }
        }

        return true;
    }

    public boolean isFree(int x, int y) {
        if (!isValid(x, y)) return false;
        int idx = toIndex(x, y);

        var b = get(x, y);
        if (b == null || b == TileType.WALL || b == TileType.DEEP_WATER || b == TileType.NOTHING) return false;
        var o = getObject(x, y);
        if (o != null) {
            b = o.getTile().type();
            if (b == null || b == TileType.WALL || b == TileType.DEEP_WATER || b == TileType.NOTHING) return false;
        }

        return actorsXY[idx] == 0;
    }

    public void move(Actor a, int toX, int toY) {
        int from = toIndex(a.getX(), a.getY());
        int to = toIndex(toX, toY);
        int actorId = actorsXY[from];

        if (actorId == 0) {
            logger.warn(name + ": Try moving from free place " + toX + ", " + toY);
        }
        if (actorsXY[to] != 0) {
            logger.warn(name + ": Try moving into occupied place " + toX + ", " + toY);
        }

        actorsXY[from] = 0;
        actorsXY[to] = actorId;
        a.setX(toX);
        a.setY(toY);

////        logger.info(name + ": Actor #" + actorId + " set x=" + toX + ", y=" + toY);
    }

    public @Nullable Coord findFreePlace(int x, int y, int maxDev) {
        int idx = findFreeIndex(x, y, maxDev);
        if (idx == -1) {
            return null;
        } else {
            return toCoord(idx);
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
    private int findFreeIndex(int x, int y, int maxDev) {

        if (!isValid(x, y)) return -1;

        if (actorsXY[toIndex(x, y)] == 0) return toIndex(x, y);

        for (int i = 1; i <= maxDev; i++) {

            if (i % 2 == 1) {
                for (int s = 0; s < i; s++) {
                    x++;
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y);
                }

                for (int s = 0; s < i; s++) {
                    y++;
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y);
                }
            } else {
                for (int s = 0; s < i; s++) {
                    x--;
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y);
                }

                for (int s = 0; s < i; s++) {
                    y--;
                    if (isValid(x, y) && isFree(x, y)) return toIndex(x, y);
                }
            }
        }

        return -1;
    }

    public String debugActors() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < actorsXY.length; i++) {

            int it = actorsXY[i];
            if (i % width == 0) {
                sb.append('\n');
                sb.append(String.format("%1$-4s", i / width + offsetY));
            }

            if (it == 0) {
                sb.append('.');
            } else if (it >= 1000) {
                sb.append('c');
            } else {
                sb.append('p');
            }

        }

        return sb.toString();
    }

    //    @Contract(pure = true)
    private boolean isValid(int x, int y) {
        return x >= offsetX && x < (offsetX + width) && y >= offsetY && y < (offsetY + height);
    }

    private int toIndex(int x, int y) {
        return x - offsetX + (y - offsetY) * width;
    }

    private Coord toCoord(int idx) {
        return new Coord(idx % width + offsetX, idx / width + offsetY);
    }

    @Override
    public String toString() {
        return debugActors();
    }

    public Collection<Actor> getAllActors() {
        return actors.values();
    }

    public Collection<Actor> getAllPlayers() {
        return actors.values();
    }
}

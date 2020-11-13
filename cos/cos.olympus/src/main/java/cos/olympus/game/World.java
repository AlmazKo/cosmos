package cos.olympus.game;


import cos.logging.Logger;
import cos.map.Coord;
import cos.map.Lands;
import cos.map.Tile;
import cos.map.TileType;
import cos.olympus.NoSpaceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static cos.olympus.game.Util.nextX;
import static cos.olympus.game.Util.nextY;
import static cos.ops.Direction.SOUTH;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class World implements TileMap, GMap {
    private final static Logger  logger = new Logger(World.class);
    private final        int     width;
    private final        int     height;
    private final        short[] basis;
    private final        short[] objects;
    private final        Tile[]  tiles;
    private final        int[]   creatures;

    private final HashMap<Integer, Creature> creatureObjects = new HashMap<>();

    private final int offsetX;
    private final int offsetY;


    public World(Lands lands) {
        this.offsetX = lands.getOffsetX();
        this.offsetY = lands.getOffsetY();
        this.width = lands.getWidth();
        this.height = lands.getHeight();
        this.basis = lands.getBasis();
        this.objects = lands.getObjects();
        this.creatures = new int[basis.length];
        this.tiles = lands.getTiles();
//        debug();
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

                char b;
                switch (tile.type()) {

                    case SHALLOW:
                        b = '~';
                        break;
                    case DEEP_WATER:
                        b = '≈';
                        break;

                    case GRASS:
                        b = '.';
                        break;
//                    case WALL:
//                        b = '#';
//                        break;
                    case GATE:
                        b = 'П';
                        break;
                    case NOTHING:
                        b = 'x';
                        break;

                    default:
                        b = 'N';
                }

                sb.append(b);
            }

        }

        System.out.println(sb.toString());
    }

    @Nullable @Override public TileType get(int x, int y) {
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

        var t = tiles[objTileId];
        if (t == null) return new Obj(idx, new Tile(objTileId, TileType.ITEM), x, y);
        ;

        return new Obj(idx, t, x, y);//todo id is hardcoded
    }

    public @Nullable Creature getCreature(int uid) {
        return creatureObjects.get(uid);
    }

    public @Override @Nullable Creature getCreature(int x, int y) {
        if (!isValid(x, y)) return null;

        return _getCreature(x, y);
    }

    @Nullable private Creature _getCreature(int x, int y) {
        int crId = creatures[toIndex(x, y)];
        return creatureObjects.get(crId);
    }

    List<@NotNull Creature> getCreatures(int centerX, int centerY, int radius) {

        ArrayList<@NotNull Creature> result = new ArrayList<>();

        for (int x = max(offsetX, centerX - radius); x <= min(centerX + radius, width + offsetX); x++) {
            for (int y = max(offsetY, centerY - radius); y <= min(centerY + radius, height + offsetY); y++) {
                if (x == centerX && y == centerY) continue;

                @Nullable Creature cr = _getCreature(x, y);
                if (cr != null) {
                    result.add(cr);
                }
            }
        }

        return result;
    }

    public void iterate(int centerX, int centerY, int radius, IntIntConsumer consumer) {
        for (int x = max(offsetX, centerX - radius); x <= min(centerX + radius, width + offsetX); x++) {
            for (int y = max(offsetY, centerY - radius); y <= min(centerY + radius, height + offsetY); y++) {
//                if (x == centerX && y == centerY) continue;
                consumer.accept(x, y);
            }
        }

    }

    public boolean isNoCreature(int id) {
        return !creatureObjects.containsKey(id);
    }

    void removeCreature(int id) {
        var cr = creatureObjects.remove(id);
        if (cr == null) {
            logger.warn("Not found creature" + id + " for removing");
            return;
        }

        int idx = toIndex(cr.x, cr.y);

        //todo debug
        if (creatures[idx] != id) {
            throw new RuntimeException("Wrong position #" + id);
        }
        creatures[idx] = 0;
    }


    public void removeCreatureIf(Predicate<? super Creature> filter) {
        creatureObjects.values().removeIf(cr -> {
            if (filter.test(cr)) {
                int idx = toIndex(cr.x, cr.y);
                creatures[idx] = 0;
                return true;
            } else {
                return false;
            }
        });
    }

    public Creature createCreature(User usr) {

        int idx = toIndex(usr.lastX, usr.lastY);
        if (idx < 0 || idx >= basis.length) {
            throw new NoSpaceException("Fail finding free place");
        }

        idx = findFreeIndex(usr.lastX, usr.lastY, 2);

        if (idx >= 0) {

            var coord = toCoord(idx);
            var cr = new Creature(usr.id, usr.name, coord.getX(), coord.getY(), (byte) 0, (byte) 0, null, SOUTH);

            creatures[idx] = cr.id;
            creatureObjects.put(cr.id, cr);

            logger.info("Creature #" + cr.id + " set x=" + cr.x + ", y=" + cr.y);
//            if (c instanceof Npc) npcs.put(c.getId(), (Npc) c);
            return cr;
        } else {
            throw new NoSpaceException("Fail finding free place");
        }
    }


    public @Override boolean isNoCreatures(int x, int y) {
        if (!isValid(x, y)) return false;

        return creatures[toIndex(x, y)] == 0;
    }

    public boolean isNoMovingCreaturesIn(int x, int y) {
        var crs = getCreatures(x, y, 1);
        for (Orientable o : crs) {
            if (o.speed() > 0 && (nextX(o) == x && nextY(o) == y)) {
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
            b = o.tile().type();
            if (b == null || b == TileType.WALL || b == TileType.DEEP_WATER || b == TileType.NOTHING) return false;
        }

        return creatures[idx] == 0;
    }

    public @Override void moveCreature(Creature cr, int toX, int toY) {
        int from = toIndex(cr.x, cr.y);
        int to = toIndex(toX, toY);
        int creatureId = creatures[from];

        if (creatureId == 0) {
            logger.warn("Try moving from free place " + toX + ", " + toY);
        }
        if (creatures[to] != 0) {
            logger.warn("Try moving into occupied place " + toX + ", " + toY);
        }

        creatures[from] = 0;
        creatures[to] = creatureId;
        cr.x = toX;
        cr.y = toY;

        logger.info("Creature #" + creatureId + " set x=" + toX + ", y=" + toY);
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

        if (creatures[toIndex(x, y)] == 0) return toIndex(x, y);

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

    public String debugCreatures() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < creatures.length; i++) {

            int it = creatures[i];
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
        return x >= offsetX && x < (offsetX + width) && y >= offsetY && x < (offsetY + height);
    }

    private int toIndex(int x, int y) {
        return x - offsetX + (y - offsetY) * width;
    }

    private Coord toCoord(int idx) {
        return new Coord(idx % width + offsetX, idx / width + offsetY);
    }

    @Override public String toString() {
        return debugCreatures();
    }

    public Collection<Creature> getAllCreatures() {
        return creatureObjects.values();
    }
}

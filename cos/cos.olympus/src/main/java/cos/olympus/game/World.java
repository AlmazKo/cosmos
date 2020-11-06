package cos.olympus.game;


import cos.logging.Logger;
import cos.map.Coord;
import cos.map.Lands;
import cos.map.Tile;
import cos.map.TileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static cos.ops.Direction.SOUTH;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class World implements TileMap, GMap {
    private final static Logger  logger = new Logger(Game.class);
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
//        this.movements = new int[basis.length];
        this.tiles = lands.getTiles();
        debug();
    }

//
//    private void iterate() {
//
//        int x = 0;
//        int y = 0;
//        int idx = 0;
//        for (int i = 0; i < creatures.length; i++) {
//            int c = creatures[i];
//            if (c == 0) continue;
//
//            int speed = c << 4;
//            if (speed == 0) continue;
//
//            int dir = c << 1;
//            int offset = c << 2;
//            int newOffset = (offset + speed) % 16;
//            if (newOffset == 1) {
//                if (creatures[idx + 1] == 0) {
//                    creatures[idx + 1] = c;
//                }
//                //try move
//            }
//        }
//    }


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
                switch (tile.getType()) {

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

//    public Player addPlayer(int id) {
//
//        int idx = findFreeIndex(-18, 0, 3);
//        if (idx == -1) throw new RuntimeException("Not found the place for player");
//
//        Coord coord = toCoord(idx);
//        Player player = new Player(id, new CreatureState(50, coord.getX(), coord.getY(), Direction.SOUTH));
//
//        creatures[idx] = id;
//        players.put(id, player);
//        return player;
//    }
//
//    public void removePlayer(int id) {
//        Player removed = players.remove(id);
//
//        if (removed != null) {
//            int idx = toIndex(removed.getX(), removed.getY());
//            int inMap = creatures[idx];
//            if (inMap != id) {
//                throw new RuntimeException("Wrong place player=" + id);
//            } else {
//                creatures[idx] = 0;
//            }
//
//        }
//    }

    @Nullable @Override public TileType get(int x, int y) {
        int idx = toIndex(x, y);
        if (idx < 0 || idx >= basis.length) return null;
        var b = basis[idx];
        var t = tiles[b];
        return (t == null) ? null : t.getType();
    }

    public @Nullable Obj getObject(int x, int y) {
        int idx = toIndex(x, y);
        if (idx < 0 || idx >= objects.length) return null;

        var objTileId = objects[idx];
        if (objTileId == 0) return null;

        return new Obj(idx, objTileId, x, y);//todo id is hardcoded
    }
//
//    @Nullable public Tile getObject(int x, int y) {
//        int idx = toIndex(x, y);
//        if (idx < 0 || idx >= basis.length) return null;
//
//        return tiles[objects[idx]];
//    }

    public  @Nullable Creature getCreature(int uid) {
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
        if (cr == null) return;

        int idx = toIndex(cr.x, cr.y);
        creatures[idx] = 0;
    }

    public Creature createCreature(User usr) {

        int idx = toIndex(usr.lastX, usr.lastY);
        if (idx < 0 || idx >= basis.length) {
            throw new IllegalStateException("Fail finding free place");
        }

        idx = findFreeIndex(usr.lastX, usr.lastY, 3);

        if (idx >= 0) {

            var coord = toCoord(idx);
            var cr = new Creature(usr.id, usr.name, coord.getX(), coord.getY(), (byte) 0, (byte) 0, null, SOUTH);

            creatures[idx] = cr.id;
            creatureObjects.put(cr.id, cr);
            logger.info("Creature #" + cr.id + " set x=" + cr.x + ", y=" + cr.y);
//            if (c instanceof Npc) npcs.put(c.getId(), (Npc) c);
            return cr;
        } else {
            throw new IllegalStateException("Fail finding free place");
        }
    }


    public @Override boolean isNoCreatures(int x, int y) {
        if (!isValid(x, y)) return false;

        return creatures[toIndex(x, y)] == 0;
    }

    public @Override void moveCreature(Creature cr, int toX, int toY) {
        moveCreature(cr.x, cr.y, toX, toY);
        cr.x = toX;
        cr.y = toY;
    }

    public @Override void moveCreature(int fromX, int fromY, int toX, int toY) {
        //        if (!isValid(x, y)) return false;
        //todo add validation

        int from = toIndex(fromX, fromY);
        int to = toIndex(toX, toY);
        int creatureId = creatures[from];
        creatures[to] = creatureId;
        logger.info("Creature #" + creatureId + " set x=" + toX + ", y=" + toY);
        creatures[from] = 0;
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
                    if (isValid(x, y) && creatures[toIndex(x, y)] == 0) return toIndex(x, y);
                }

                for (int s = 0; s < i; s++) {
                    y++;
                    if (isValid(x, y) && creatures[toIndex(x, y)] == 0) return toIndex(x, y);
                }
            } else {
                for (int s = 0; s < i; s++) {
                    x--;
                    if (isValid(x, y) && creatures[toIndex(x, y)] == 0) return toIndex(x, y);
                }

                for (int s = 0; s < i; s++) {
                    y--;
                    if (isValid(x, y) && creatures[toIndex(x, y)] == 0) return toIndex(x, y);
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

    public void cleanDeadCreatures() {
//        npcs.values().removeIf(n -> {
//            if (n.isDead()) {
//
//                this.creatures[toIndex(n.getX(), n.getY())] = 0;
//                return true;
//            }
//
//            return false;
//        });
    }

    public Collection<Creature> getAllCreatures() {
        return creatureObjects.values();
    }
}

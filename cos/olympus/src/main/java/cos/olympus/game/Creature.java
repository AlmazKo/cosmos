package cos.olympus.game;

import cos.map.CreatureType;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static cos.olympus.game.Movements.METER;
import static cos.ops.Direction.*;

public final class Creature implements Orientable {
    final Avatar avatar;
    int lastSpellTick;
    int x;
    int y;
    int offset;
    int speed;
    @Nullable Direction mv = null;

    Direction sight;
    Metrics metrics;
    Bag bag = new Bag();
    Map<Integer, Obj> zoneObjects = new HashMap<>();
    Map<Integer, Orientation> zoneCreatures = new HashMap<>();
    Map<Integer, Metrics> zoneMetrics = new HashMap<>();
    Map<Integer, SpellStrategy> zoneSpells = new HashMap<>();

    public Creature(Avatar avatar, int x, int y, int offset, int speed, @Nullable Direction dir, Direction sight, int life) {
        this.avatar = avatar;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.speed = speed;
        this.mv = dir;
        this.sight = sight;
        this.metrics = new Metrics(avatar.id(), life);
    }

    public void setSight(Direction sight) {
        this.sight = sight;
    }

    Orientation orientation() {
        return new Orientation(avatar.id(), x, y, speed, offset, sight, mv);
    }

    Metrics copyMetrics() {
        return metrics.copy();
    }

    @Override public String toString() {
        return "Creature{" +
               "id=" + avatar.id() +
               ", lvl=" + metrics.lvl +
               ", life=" + metrics.life +
               ", type=" + type() +
//                ", name='" + name + '\'' +
//                ", x=" + x +
//                ", y=" + y +
               ", pos=[" + rx() + "; " + ry() + "]" +
               ", speed=" + speed +
               ", dir=" + mv +
               ", sight=" + sight +
               '}';
    }

    public float ry() {
        if (mv == NORTH) return y - ((float) offset / METER);
        if (mv == SOUTH) return y + ((float) offset / METER);
        return y;
    }

    public float rx() {
        if (mv == WEST) return x - ((float) offset / METER);
        if (mv == EAST) return x + ((float) offset / METER);
        return x;
    }

    public void stop() {
        offset = 0;
        speed = 0;
        mv = null;
    }

    public void damage(Damage d) {
        this.metrics.minus(d.amount());
    }

    public boolean isDead() {
        return metrics.isDead();
    }

    public int id() {
        return avatar.id();
    }

    public CreatureType type() {
        return avatar.type();
    }

    @Override public int x() {
        return x;
    }

    @Override public int y() {
        return y;
    }

    @Override public int speed() {
        return speed;
    }

    @Override public int offset() {
        return offset;
    }

    @Override public @Nullable Direction mv() {
        return mv;
    }

    @Override public Direction sight() {
        return sight;
    }

    public int life() {
        return metrics.life();
    }

    public void onKill(Death death) {

        if (death.victim().metrics.lvl > metrics.lvl) {
            metrics.exp += 3;
        } else if (death.victim().metrics.lvl >= metrics.lvl - 1) {
            metrics.exp += 1;
        }

        if (metrics.exp >= 10) {
            metrics.lvl++;
            metrics.exp = metrics.exp - 10;
            metrics.maxLife = (int) (metrics.maxLife + 1.2);
            metrics.life = metrics.maxLife;
            System.out.println("" + this + " level up");
        }
    }
}

package cos.olympus.game;

public class Usr {

    final int id;
    String worldName;

    public Usr(int id, String worldName) {
        this.id = id;
        this.worldName = worldName;
    }

    @Override public String toString() {
        return "Usr{" +
               "id=" + id +
               ", worldName='" + worldName + '\'' +
               '}';
    }
}

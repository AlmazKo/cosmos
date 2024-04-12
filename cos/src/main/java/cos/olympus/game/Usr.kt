package cos.olympus.game;

public class Usr {

    public final int id;
    public final String name;
    public String worldName;

    public Usr(int id, String worldName) {
        this.id = id;
        this.name = "user:" + id;
        this.worldName = worldName;
    }

    @Override
    public String toString() {
        return "Usr{" +
                "id=" + id +
                ", worldName='" + worldName + '\'' +
                '}';
    }
}

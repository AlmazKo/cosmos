package cos;

public final class Env {
    public static String get(String name) {
        var value = System.getenv(name);
        if (value == null) {
            return System.getProperty(name);
        } else {
            return value;
        }
    }
}

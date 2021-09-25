package cos.logging;

public final class LogConfig {
    public static int BUILD_ID = -1;
    public static final long LAUNCH_TS = System.currentTimeMillis() / 1000;
    static final boolean APPEND_FILE = "true".equalsIgnoreCase(System.getProperty("FxTraceLogs"));
    static final String ES_HOST = System.getProperty("FxEsHost", "");
    static final String ES_INDEX = System.getProperty("FxEsIndex", "");

    static boolean isEsLogger() {
        return !ES_HOST.isBlank() && !ES_INDEX.isBlank();
    }

    static {
        if (isEsLogger()) {
            new cos.logging.SharedLogger(LogConfig.class).info("Use ES logger: " + ES_HOST + " #" + ES_INDEX);
        }
    }
}

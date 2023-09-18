package cos.logging;


import static cos.logging.Logger.Level.INFO;

public final class LogConfig {
    public static int BUILD_ID = -1;
    public static final String ENV = System.getProperty("FxEnvName", "LOCAL");
    public static final long LAUNCH_TS = System.currentTimeMillis() / 1000;
    static final boolean APPEND_FILE = "true".equalsIgnoreCase(System.getProperty("FxTraceLogs"));
    static final boolean APPEND_CONTEXT = true;
    static final String ES_HOST = System.getProperty("FxEsHost", "");
    static final String ES_INDEX = System.getProperty("FxEsIndexPrefix", "fx2");
    static LogHandler[] HANDLERS = new LogHandler[0];
    public static final Logger.Level DEFAULT_LEVEL = Logger.Level.valueOf(System.getenv().getOrDefault("FX_LOG_DEFAULT_LEVEL", INFO.name()));

    static boolean isEsLogger() {
        return !ES_HOST.isBlank();
    }

    static {
        if (isEsLogger()) {
            new SharedLogger(LogConfig.class).info("Use ES logger: " + ES_HOST);
        }
    }

    public static void register(LogHandler... handlers) {
        HANDLERS = handlers;
    }
}

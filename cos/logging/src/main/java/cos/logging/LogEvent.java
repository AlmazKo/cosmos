package cos.logging;

public final class LogEvent {
    long timestamp;
    Logger.Level level;
    String thread;
    String stackTrace;
    String loggerName;
    String message;
    String subType;
    String tag;
    String rid;
    String env;
    long launchTs;
    long threadId;
    int buildId;
    int accountId;
    String type;
    int execTime;
    String action;

    public LogEvent() {
    }
}


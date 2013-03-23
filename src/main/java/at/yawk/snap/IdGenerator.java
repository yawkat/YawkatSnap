package at.yawk.snap;

import java.util.Properties;

public interface IdGenerator {
    public String generateId(long timeMilliSeconds, long timeSeconds);

    void serializeSettings(Properties properties);
}

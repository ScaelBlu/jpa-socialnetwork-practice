package socialnetwork;

import java.time.LocalDateTime;

public class TimeMachine {

    private static final TimeMachine INSTANCE = new TimeMachine();
    private static LocalDateTime NOW;

    private TimeMachine() {
    }

    public static LocalDateTime now() {
        return NOW;
    }

    public static void set(LocalDateTime NOW) {
        TimeMachine.NOW = NOW;
    }

    public static boolean isSet() {
        return NOW != null;
    }

    public static void clear() {
        NOW = null;
    }
}

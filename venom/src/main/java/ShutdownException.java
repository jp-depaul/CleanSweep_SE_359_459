public class ShutdownException extends Exception {
    public static final String OUT_OF_POWER = "BATTERY DEPLETED";
    public static final String STATION_OBSTRUCTED = "EXPECTED PATH TO STATION WAS OBSTRUCTED";
    public ShutdownException(String msg) {
        super(msg);
    }
}

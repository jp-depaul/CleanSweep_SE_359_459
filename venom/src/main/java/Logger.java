import java.awt.*;

public class Logger {

    private String fullLog = "";
    private boolean showCoordinates = true;
    private boolean printOnNewAction = true;

    public Logger() {

    }
    private void log(String msg) {
        fullLog += msg + "\n";
        if (printOnNewAction) {
            System.out.println(msg);
        }
    }
    private String pointToString(Point point) {
        return "[" + point.x + "," + point.y + "]";
    }
    private String getIndent(int count) {
        return "    ".repeat(Math.max(0, count));
    }
    //
    // BASIC ACTIONS :
    //
    public void logMove(Point from, Point to) {
        String msg = getIndent(1) + "MOVING " + Util.getOrientation(from, to).name();
        if (showCoordinates) {
            msg += " : " + pointToString(from) + " -> " + pointToString(to);
        }
        log(msg);
    }
    public void logCharge(Point point) {
        String msg = getIndent(1) + "RECHARGING";
        if (showCoordinates) {
            msg += " : " + pointToString(point);
        }
        log(msg);
    }
    public void logClean(Point point) {
        String msg = getIndent(1) + "CLEANING";
        if (showCoordinates) {
            msg += " : " + pointToString(point);
        }
        log(msg);
    }
    public void logShiftMode(Point point, int before, int after) {
        String msg = getIndent(1) + "SHIFTING CLEANING MODE [LV" + before + " -> LV" + after + "]";
        if (showCoordinates) {
            msg += " : " + pointToString(point);
        }
        log(msg);
    }
    //
    // PHASE INDICATION :
    //
    public void logBeginMap(Point point) {
        String msg = "BEGINNING MAP CYCLE";
        if (showCoordinates) {
            msg += " : FORMER " + pointToString(point) + " IS NEW ORIGIN " + pointToString(new Point(0,0));
        }
        log(msg);
    }
    public void logBeginClean() {
        String msg = "BEGINNING CLEAN CYCLE";
        log(msg);
    }
    public void logEndMap() {
        String msg = "MAP CYCLE COMPLETE";
        log(msg);
    }
    public void logEndClean() {
        String msg = "CLEAN CYCLE COMPLETE";
        log(msg);
    }
    //
    // SCANNER DISCOVERY
    //
    public void logDiscoveredNewCell(Point target, boolean isStation) {
        String msg = getIndent(1) + "DISCOVERED CELL";
        if (isStation) {
            msg += " (WITH STATION)";
        }
        if (showCoordinates) {
            msg += " : " + pointToString(target);
        }
        log(msg);
    }
    //
    // ERROR SHUTDOWN
    //
    public void logOutOfCharge(Point point) {
        String msg = "OUT OF CHARGE, SHUTTING DOWN";
        if (showCoordinates) {
            msg += " : " + pointToString(point);
        }
        log(msg);
    }
    public void logStationObstructed(Point point) {
        String msg = "EXPECTED PATH TO STATION WAS OBSTRUCTED, SHUTTING DOWN";
        if (showCoordinates) {
            msg += " : " + pointToString(point);
        }
        log(msg);
    }
}

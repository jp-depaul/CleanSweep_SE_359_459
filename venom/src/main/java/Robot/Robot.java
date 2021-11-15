package Robot;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Predicate;

public class Robot {

  private final VirtualFloor FLOOR;
  private final Logger LOGGER;
  private final HashMap<Point, Cell> CELL_MAP = new HashMap<Point, Cell>();
  private final Point ORIGIN = new Point(0, 0);
  private final double CHARGE_CAPACITY = 250;
  private final int DIRT_CAPACITY = 50;

  private double chargeLevel = CHARGE_CAPACITY;
  private int dirtLevel = 0;
  private boolean isFull = false;
  private int cleaningMode = 1;
  private Point currentPoint = ORIGIN;
  private boolean hasMap = false;

  public Robot(VirtualFloor virtualFloor) {
    FLOOR = virtualFloor;
    LOGGER = new Logger();
  }

  // Primary Functions
  public void mapFloor() throws ShutdownException {
    LOGGER.logBeginMap(currentPoint);
    CELL_MAP.clear();
    currentPoint = ORIGIN;
    CELL_MAP.put(currentPoint, new Cell());
    ArrayList<Point> pathToUnvisited;
    do {
      scan();
      ArrayList<Point> pathToStation = getMinPathToPointWhere(Cell::isStation);
      pathToUnvisited = getMinPathToPointWhere(Cell::isUnvisited);
      if (pathToUnvisited == null) {
        break;
      } else if (chargeLevel <= getPathCost(pathToStation, true)
              + getPathCost(pathToUnvisited, true) * 2 + 3
      ) {
        returnAndCharge();
      } else {
        moveTo(pathToUnvisited.get(0));
      }
    } while (true);
    returnAndCharge();
    LOGGER.logEndMap();
    hasMap = true;
  }

  public void cleanFloor() throws ShutdownException, InterruptedException {
    // TODO: check conditions
    if (CELL_MAP.isEmpty()) {
      return;
    }
    LOGGER.logBeginClean();
    for (Cell c : CELL_MAP.values()) {
      c.setMarked(true);
    }
    ArrayList<Point> pathToMarked;
    do {
      scan();
      ArrayList<Point> pathToStation = getMinPathToPointWhere(Cell::isStation);
      pathToMarked = getMinPathToPointWhere(Cell::isMarked);
      if (pathToMarked == null) {
        break;
      } else if (chargeLevel <= getPathCost(pathToStation, true)
              + getPathCost(pathToMarked, true) * 2 + 3
      ) {
        returnAndCharge();
      } else if (dirtLevel >= DIRT_CAPACITY) {
        isFull = true;
        returnAndCharge();
        emptyDirt();
      } else if (pathToMarked.isEmpty()) {
        clean();
        scan();
        if (!CELL_MAP.get(currentPoint).needsCleaning()) {
          CELL_MAP.get(currentPoint).setMarked(false);
        }
      } else {
        moveTo(pathToMarked.get(0));
      }
    } while (true);
    returnAndCharge();
    LOGGER.logEndClean();
  }

  // Supplementary Functions
  public void returnAndCharge() throws ShutdownException {
    ArrayList<Point> path = getMinPathToPointWhere(Cell::isStation);
    if (path == null) {
      LOGGER.logStationObstructed(currentPoint);
      throw new ShutdownException(ShutdownException.STATION_OBSTRUCTED);
    }
    for (Point p : path) {
      Dir d = Util.getOrientation(currentPoint, p);
      if (CELL_MAP.get(currentPoint).border.get(Util.getOrientation(currentPoint, p)) != CellBorder.OPEN) {
        LOGGER.logStationObstructed(currentPoint);
        throw new ShutdownException(ShutdownException.STATION_OBSTRUCTED);
      }
      moveTo(p);
      scan();
    }
    charge();
  }

  // Logged Actions
  private void scan() {

    // preserve marking, update current, set as visited
    boolean wasMarked = CELL_MAP.get(currentPoint).isMarked();
    CELL_MAP.put(currentPoint, FLOOR.getCellCopyFromOriginRelativePoint(currentPoint));
    CELL_MAP.get(currentPoint).setMarked(wasMarked);

    // sync borders with neighboring cells
    for (Dir dir1 : Util.ALL_DIR) {
      Point adjacentPoint = getAdjacentPoint(currentPoint, dir1);

      // IF neighbor found : sync border with neighbor
      if (CELL_MAP.containsKey(adjacentPoint)) {
        CELL_MAP.get(adjacentPoint).border.put(Util.reverseDir(dir1), CELL_MAP.get(currentPoint).border.get(dir1));
      }

      // ELSE IF no neighbor but open path : create neighbor
      else if (CELL_MAP.get(currentPoint).border.get(dir1) == CellBorder.OPEN && !CELL_MAP.containsKey(adjacentPoint)) {
        //logger.logDiscoveredNewCell(adjacentPoint, );
        CELL_MAP.put(adjacentPoint, new Cell());
        for (Dir dir2 : Util.ALL_DIR) {
          Point adjacentAdjacentPoint = getAdjacentPoint(adjacentPoint, dir2);
          if (CELL_MAP.containsKey(adjacentAdjacentPoint)) {
            CELL_MAP.get(adjacentPoint).border.put(dir2, CELL_MAP.get(adjacentAdjacentPoint).border.get(Util.reverseDir(dir2)));
          }
        }
      }

    }
  }
  private void clean() throws ShutdownException {
    int floorLevel = CELL_MAP.get(currentPoint).getFloorLevel();
    if (cleaningMode != floorLevel) {
      LOGGER.logShiftMode(currentPoint, cleaningMode, floorLevel);
      cleaningMode = floorLevel;
    }
    LOGGER.logClean(currentPoint);
    chargeLevel -= cleaningMode;
    FLOOR.cleanCellAtOriginRelativePoint(currentPoint);
    dirtLevel++;
    if (chargeLevel < 0) {
      LOGGER.logOutOfCharge(currentPoint);
      throw new ShutdownException("");
    } else if (dirtLevel > DIRT_CAPACITY) {
      LOGGER.maxDirtCapacityExceeded(currentPoint);
      throw new ShutdownException("");
    }
  }
  private void moveTo(Point target) {
    LOGGER.logMove(currentPoint, target);
    chargeLevel -= ((double) CELL_MAP.get(currentPoint).getCost() + (double) CELL_MAP.get(target).getCost()) / 2;
    currentPoint = target;
    if (chargeLevel < 0) {
      LOGGER.logOutOfCharge(currentPoint);
    }
  }
  private void charge() {
    chargeLevel = CHARGE_CAPACITY;
    LOGGER.logCharge(currentPoint);
  }
  private void emptyDirt() {
    while (isFull) {
      Scanner s = new Scanner(System.in);
      System.out.println("Dirt container is full: [e] to empty vacuum");
      String input = s.nextLine();
      if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("empty")) {
        dirtLevel = 0;
        isFull = false;
      }
    }
  }

  // Utility Functions
  public void getStatus() {
    final String tab = "    ";
    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss z");
    System.out.println(tab + "Time: " + formatter.format(new Date(System.currentTimeMillis())));
    System.out.println(tab + "Origin-relative position: [" + currentPoint.x + "," + currentPoint.y + "]");
    System.out.println(tab + "Charge level: " + chargeLevel + "/" + CHARGE_CAPACITY);
    System.out.println(tab + "Dirt level: " + dirtLevel + "/" + DIRT_CAPACITY);
  }
  public void setPositionForTesting(Point p) {
    currentPoint = p;
  }
  public ArrayList<Point> getMinPathToPointWhere(Predicate<Cell> condition) {
    ArrayList<Point> path = new ArrayList<Point>();
    if (!condition.test(CELL_MAP.get(currentPoint))) {
      path.add(currentPoint);
      path = getMinPathToPointWhereHelper(condition, path);
    }
    if (path != null && !path.isEmpty()) {
      path.remove(0);
    }
    return path;
  }
  private ArrayList<Point> getMinPathToPointWhereHelper(Predicate<Cell> condition, ArrayList<Point> path) {
    if (condition.test(CELL_MAP.get(path.get(path.size() - 1)))) {
      return path;
    }
    ArrayList<Point> shortest = null;
    for (Dir dir : Util.ALL_DIR) {
      if (CELL_MAP.get(path.get(path.size() - 1)).border.get(dir) == CellBorder.OPEN) {
        Point p = getAdjacentPoint(path.get(path.size() - 1), dir);
        if (!path.contains(p) && CELL_MAP.containsKey(p)) {
          ArrayList<Point> newPath = new ArrayList<Point>(path);
          newPath.add(p);
          ArrayList<Point> result = getMinPathToPointWhereHelper(condition, newPath);
          if (result != null && (shortest == null
                  || getPathCost(result, false) < getPathCost(shortest, false))) {
            shortest = result;
          }
        }
      }
    }
    return shortest;
  }
  public double getPathCost(ArrayList<Point> path, boolean addCurrentPoint) {
    double cost = 0.0;
    if (path.isEmpty()) {
      return cost;
    }
    if (addCurrentPoint) {
      cost += (double) (CELL_MAP.get(currentPoint).getCost() + CELL_MAP.get(path.get(0)).getCost()) / 2;
    }
    if (path.size() > 1) {
      for (int i = 0; i <= path.size() - 2; i++) {
        cost += (double) (CELL_MAP.get(path.get(i)).getCost() + CELL_MAP.get(path.get(i + 1)).getCost()) / 2;
      }
    }
    return cost;
  }
  private Point getAdjacentPoint(Point pos, Dir dir) {
    switch (dir) {
      case NORTH:
        return new Point(pos.x, pos.y + 1);
      case SOUTH:
        return new Point(pos.x, pos.y - 1);
      case EAST:
        return new Point(pos.x + 1, pos.y);
      case WEST:
        return new Point(pos.x - 1, pos.y);
    }
    return pos;
  }
  public boolean hasMap() { return hasMap; }
  public boolean isFull() {
    return isFull;
  }
  public void setLogging(boolean active) { LOGGER.printOnNewAction = active; }
}

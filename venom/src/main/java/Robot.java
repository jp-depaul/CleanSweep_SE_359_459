import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import static java.lang.System.exit;

public class Robot {

  private final VirtualFloor FLOOR;
  private final Logger LOGGER;
  private final HashMap<Point, Cell> CELL_MAP = new HashMap<Point, Cell>();
  private final Point ORIGIN = new Point(0, 0);
  private final int CHARGE_CAPACITY = 250;//250;
  private final int DIRT_CAPACITY = 50;

  private double chargeLevel = CHARGE_CAPACITY;
  private int dirtLevel = 0;
  private ArrayList<Point> stations; // not necessarily mapped
  private Point currentPoint = ORIGIN;

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
      }
      else if (chargeLevel <= getPathCost(pathToStation, true)
              + getPathCost(pathToUnvisited, true) * 2 + 3
      ) {
        returnAndCharge();
      }
      else {
        moveTo(pathToUnvisited.get(0));
      }
    } while (true);
    returnAndCharge();
    LOGGER.logEndMap();
  }

  public void cleanFloor() throws ShutdownException {
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
      }
      else if (chargeLevel <= getPathCost(pathToStation, true)
              + getPathCost(pathToMarked, true) * 2 + 3
      ) {
        returnAndCharge();
      }
      else if (pathToMarked.isEmpty()) {
        clean();
        scan();
        if (!CELL_MAP.get(currentPoint).needsCleaning()) {
          CELL_MAP.get(currentPoint).setMarked(false);
        }
      }
      else {
        moveTo(pathToMarked.get(0));
      }
    } while (true);
    returnAndCharge();
    LOGGER.logEndClean();
  }

  // Supplementary Functions
  public void detectNewStations() {
    // Stations can be detected up to 2 cells away
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        if (!(Math.abs(x) == 2 && Math.abs(y) == 2) || (x != 0 && y != 0)) {
          Point p = new Point(currentPoint.x + x, currentPoint.y + y);
          Cell c = FLOOR.getCellCopyFromOriginRelativePoint(p);
          if (!stations.contains(p) && c.isStation()) {
            stations.add(p);
            // TODO: Detected station message
          }
        }
      }
    }
  }
  public void returnAndCharge() {
    ArrayList<Point> path = getMinPathToPointWhere(Cell::isStation);
    if (path == null) {
      LOGGER.logStationObstructed(currentPoint);
      exit(-1);
    }
    for (Point p : path) {
      Dir d = Util.getOrientation(currentPoint, p);
      if (CELL_MAP.get(currentPoint).border.get(Util.getOrientation(currentPoint, p)) != CellBorder.OPEN) {
        LOGGER.logStationObstructed(currentPoint);
        exit(-1);
      }
      moveTo(p);
      scan();
    }
    charge();
  }

  // Logged Actions
  private void logSuccess() {

  }
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
    LOGGER.logClean(currentPoint);
    chargeLevel -= CELL_MAP.get(currentPoint).getCost();
    FLOOR.cleanCellAtOriginRelativePoint(currentPoint);
    if (chargeLevel < 0) {
      LOGGER.logOutOfCharge(currentPoint);
      throw new ShutdownException("");
    }
  }
  private void moveTo(Point target) {
    LOGGER.logMove(currentPoint, target);
    // TODO: shift mode
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

  // Utility Functions
  private ArrayList<Point> getMinPathToPointWhere(Predicate<Cell> condition) {
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
  private double getPathCost(ArrayList<Point> path, boolean addCurrentPoint) {
    double cost = 0.0;
    if (path.isEmpty()) {
      return cost;
    }
    if (addCurrentPoint) {
      cost += (double)(CELL_MAP.get(currentPoint).getCost() + CELL_MAP.get(path.get(0)).getCost()) / 2;
    }
    for (int i = 0; i < path.size() - 2; i++) {
      cost += (double)(CELL_MAP.get(path.get(i)).getCost() + CELL_MAP.get(path.get(i + 1)).getCost()) / 2;
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
}

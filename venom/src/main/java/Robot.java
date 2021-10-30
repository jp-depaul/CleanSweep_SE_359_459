import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import static java.lang.System.exit;

public class Robot {

  private VirtualFloor virtualFloor;
  private final int CHARGE_CAPACITY = 30;//250;
  private final int DIRT_CAPACITY = 50;
  private double chargeLevel = CHARGE_CAPACITY;
  private int dirtLevel;
  private ArrayList<Point> stations; // not necessarily mapped

  private final Dir[] ALL_DIR = new Dir[]{Dir.NORTH, Dir.SOUTH, Dir.EAST, Dir.WEST};
  private HashMap<Point, Cell> cellMap = new HashMap<Point, Cell>();
  private final Point ORIGIN = new Point(0, 0);
  private Point currentPoint = ORIGIN;

  public Robot(VirtualFloor virtualFloor) {
    this.virtualFloor = virtualFloor;
  }

  // Primary Functions
  public void mapFloor() {
    cellMap.clear();
    currentPoint = ORIGIN;
    cellMap.put(currentPoint, new Cell());
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
    System.out.println("return ---------------");
    returnAndCharge();
    System.out.println("DONE. ---------------");
  }

  public void cleanFloor() {
    // TODO: check conditions
    if (cellMap.isEmpty()) {
      return;
    }
    for (Cell c : cellMap.values()) {
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
        if (!cellMap.get(currentPoint).needsCleaning()) {
          cellMap.get(currentPoint).setMarked(false);
        }
      }
      else {
        moveTo(pathToMarked.get(0));
      }
    } while (true);
    returnAndCharge();
    System.out.println("DONE. ---------------");
  }

  // Supplementary Functions
  public void detectNewStations() {
    // Stations can be detected up to 2 cells away
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        if (!(Math.abs(x) == 2 && Math.abs(y) == 2) || (x != 0 && y != 0)) {
          Point p = new Point(currentPoint.x + x, currentPoint.y + y);
          Cell c = virtualFloor.getCellCopyFromOriginRelativePoint(p);
          if (!stations.contains(p) && c.isStation()) {
            stations.add(p);
            // TODO: Detected station message
          }
        }
      }
    }
  }
  public void returnAndCharge() {
    final String ERR_MSG = "Expected path to station was obstructed";
    ArrayList<Point> path = getMinPathToPointWhere(Cell::isStation);
    if (path == null) {
      System.out.println("A");
      errorShutDown(ERR_MSG);
      exit(-1);
    }
    for (Point p : path) {
      Dir d = getOrientation(currentPoint, p);
      if (cellMap.get(currentPoint).border.get(getOrientation(currentPoint, p)) != CellBorder.OPEN) {
        System.out.println(p.x + "," + p.y);
        errorShutDown(ERR_MSG);
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
  private void errorShutDown(String msg) {
    System.out.println("ERROR: " + msg);
  }
  private void scan() {
    // preserve marking, update current, set as visited
    boolean wasMarked = cellMap.get(currentPoint).isMarked();
    cellMap.put(currentPoint, virtualFloor.getCellCopyFromOriginRelativePoint(currentPoint));
    cellMap.get(currentPoint).setMarked(wasMarked);

    // sync borders with neighboring cells
    for (Dir dir1 : ALL_DIR) {
      Point adjacentPoint = getAdjacentPoint(currentPoint, dir1);

      // IF neighbor found : sync border with neighbor
      if (cellMap.containsKey(adjacentPoint)) {
        cellMap.get(adjacentPoint).border.put(Util.reverseDir(dir1), cellMap.get(currentPoint).border.get(dir1));
      }

      // ELSE IF no neighbor but open path : create neighbor
      else if (cellMap.get(currentPoint).border.get(dir1) == CellBorder.OPEN && !cellMap.containsKey(adjacentPoint)) {
        cellMap.put(adjacentPoint, new Cell());
        for (Dir dir2 : ALL_DIR) {
          Point adjacentAdjacentPoint = getAdjacentPoint(adjacentPoint, dir2);
          if (cellMap.containsKey(adjacentAdjacentPoint)) {
            cellMap.get(adjacentPoint).border.put(dir2, cellMap.get(adjacentAdjacentPoint).border.get(Util.reverseDir(dir2)));
          }
        }
      }

    }
  }

  private void clean() {
    chargeLevel -= cellMap.get(currentPoint).getCost();
    virtualFloor.cleanCellAtOriginRelativePoint(currentPoint);
    System.out.println("Cleaned at " + currentPoint.x + "," + currentPoint.y);
    if (chargeLevel < 0) {
      errorShutDown("Out of charge");
    }
  }
  private void moveTo(Point target) {
    // check length = 1
    // shift mode
    chargeLevel -= ((double)cellMap.get(currentPoint).getCost() + (double)cellMap.get(target).getCost()) / 2;
    currentPoint = target;
    System.out.println("Moved to " + target.x + "," + target.y);
    if (chargeLevel < 0) {
      errorShutDown("Out of charge");
    }
    if (target.x == 0 && target.y == 1) {
      System.out.print("");
    }
  }
  private void charge() {
    System.out.println("==Charged==");
    chargeLevel = CHARGE_CAPACITY;
  }

  // Utility Functions
  private Dir getOrientation(Point from, Point to) {
    if (from.x == to.x && from.y + 1 == to.y) {
      return Dir.NORTH;
    }
    if (from.x == to.x && from.y == to.y + 1) {
      return Dir.SOUTH;
    }
    if (from.x + 1 == to.x && from.y == to.y) {
      return Dir.EAST;
    }
    if (from.x == to.x + 1 && from.y == to.y) {
      return Dir.WEST;
    }
    System.out.println("ERROR: invalid move target");
    exit(-1);
    return null;
  }

  private ArrayList<Point> getMinPathToPointWhere(Predicate<Cell> condition) {
    ArrayList<Point> path = new ArrayList<Point>();
    if (!condition.test(cellMap.get(currentPoint))) {
      path.add(currentPoint);
      path = getMinPathToPointWhereHelper(condition, path);
    }
    if (path != null && !path.isEmpty()) {
      path.remove(0);
    }
    return path;
  }
  private ArrayList<Point> getMinPathToPointWhereHelper(Predicate<Cell> condition, ArrayList<Point> path) {
    if (condition.test(cellMap.get(path.get(path.size() - 1)))) {
      return path;
    }
    ArrayList<Point> shortest = null;
    for (Dir dir : ALL_DIR) {
      if (cellMap.get(path.get(path.size() - 1)).border.get(dir) == CellBorder.OPEN) {
        Point p = getAdjacentPoint(path.get(path.size() - 1), dir);
        if (!path.contains(p) && cellMap.containsKey(p)) {
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
      cost += (double)(cellMap.get(currentPoint).getCost() + cellMap.get(path.get(0)).getCost()) / 2;
    }
    for (int i = 0; i < path.size() - 2; i++) {
      cost += (double)(cellMap.get(path.get(i)).getCost() + cellMap.get(path.get(i + 1)).getCost()) / 2;
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
  private boolean isOrigin(Point point) {
    return point.equals(ORIGIN);
  }
}

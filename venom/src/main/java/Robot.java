import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Robot {

  private VirtualFloor virtualFloor;
  private final int CHARGE_CAPACITY = 250;
  private final int DIRT_CAPACITY = 50;
  private int chargeLevel;
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
    cellMap.put(ORIGIN, new Cell());
    scan();
    ArrayList<Point> path = getMinPathToPointWhere(Cell::containsUnknown);
    while (path != null) {
      moveTo(path.get(0));
      System.out.println(currentPoint);
      scan();
      path = getMinPathToPointWhere(Cell::containsUnknown);
    }

    // work in progress

  }

  // Supplementary Functions
  public void detectNewStations() {
    // Stations can be detected up to 2 cells away
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        if (!(Math.abs(x) == 2 && Math.abs(y) == 2) || (x != 0 && y != 0)) {
          Point p = new Point(currentPoint.x + x, currentPoint.y + y);
          Cell c = virtualFloor.getCellAtOriginRelativePos(p);
          if (!stations.contains(p) && c.isStation()) {
            stations.add(p);
            // TODO: Detected station message
          }
        }
      }
    }
  }
  public void chargeAndReturnTo(Point pos) {
    ArrayList<Point> path = getMinPathToPointWhere(Cell::isStation);
    for (Point p : path) {

    }


  }

  // Logged Actions
  private void scan() {
    // update current
    Cell current = virtualFloor.getCellAtOriginRelativePos(currentPoint);
    cellMap.put(currentPoint, current);
    // sync or discover adjacent
    syncBordersWithAdjacent();

  }
  private void clean() {
    // power
  }
  private void moveTo(Point target) {
    // check length = 1
    // shift mode
    currentPoint = target;
    // power

  }
  private void charge() {

  }

  // Utility Functions
  private void syncBordersWithAdjacent() {
    Cell currentCell = cellMap.get(currentPoint);
    for (Dir dir : ALL_DIR) {
      Point adjacentPoint = getAdjacentPoint(currentPoint, dir);
      if (currentCell.border.get(dir) == CellBorder.OPEN && !cellMap.containsKey(adjacentPoint)) {
        createUnknownCellAt(adjacentPoint);
      }
      else if (cellMap.containsKey(adjacentPoint)) {
        cellMap.get(adjacentPoint).border.put(Util.reverseDir(dir), currentCell.border.get(dir));
      }
    }
  }
  private void createUnknownCellAt(Point point) {
    if (!cellMap.containsKey(point)) {
      cellMap.put(point, new Cell());
      // match border with adjacent cells
      for (Dir dir : ALL_DIR) {
        Point adjacentPoint = getAdjacentPoint(point, dir);
        if (cellMap.containsKey(adjacentPoint)) {
          CellBorder border = cellMap.get(adjacentPoint).border.get(Util.reverseDir(dir));
          cellMap.get(point).border.put(dir, border);
        }
      }
    }
  }
  private ArrayList<Point> getMinPathToPointWhere(Predicate<Cell> condition) {
    ArrayList<Point> path = new ArrayList<Point>();
    path.add(currentPoint);
    path = getMinPathToPointWhereHelper(condition, path);
    if (path != null) {
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
          if (result != null && (shortest == null || getPathCost(result) < getPathCost(shortest))) {
            shortest = result;
          }
        }
      }
    }
    return shortest;
  }
  private int getPathCost(ArrayList<Point> path) {
    int cost = 0;
    for (Point p : path) {
      if (cellMap.get(p).getCost() > 0) {
        cost += cellMap.get(p).getCost();
      }
      else {
        cost += 1;
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

}

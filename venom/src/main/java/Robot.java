import java.awt.Point;
import java.util.HashMap;

public class Robot {
  private VirtualFloor virtualFloor;
  private final int CHARGE_CAPACITY = 250;
  private final int DIRT_CAPACITY = 50;
  private int chargeLevel;
  private int dirtLevel;

  private HashMap<Point, Cell> cellMap = new HashMap<Point, Cell>();
  private final Point ORIGIN = new Point(0,0);
  private Point currentPos = ORIGIN;

  public Robot(VirtualFloor virtualFloor) {
    this.virtualFloor = virtualFloor;
    scanCurrentPos();
  }

  public int minPathToStation() {
    return -1;
  }

  private void scanCurrentPos() {
    cellMap.put(currentPos, virtualFloor.scanCellAtOriginRelativePos(currentPos));
    // TODO: log event
  }
  private void moveInDirection(Dir dir) {
    switch (dir) {
      case NORTH:
        currentPos.y++;
        break;
      case SOUTH:
        currentPos.y--;
        break;
      case EAST:
        currentPos.x++;
          break;
      case WEST:
        currentPos.x--;
        break;
    }
    // TODO: log event
  }

  private void cleanCurrentPos() {
    // TODO: log event
  }
}

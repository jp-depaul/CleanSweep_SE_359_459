import java.awt.*;

import static java.lang.System.exit;

public class Util {
  public static final Dir[] ALL_DIR = new Dir[]{Dir.NORTH, Dir.SOUTH, Dir.EAST, Dir.WEST};
  public static Dir reverseDir(Dir dir) {
    switch (dir) {
      case NORTH:
        return Dir.SOUTH;
      case SOUTH:
        return Dir.NORTH;
      case EAST:
        return Dir.WEST;
      case WEST:
        return Dir.EAST;
    }
    return Dir.NORTH;
  }
  public static Dir getOrientation(Point from, Point to) {
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
}

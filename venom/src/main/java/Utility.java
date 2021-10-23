public class Utility {
  public static Dir getOppositeDirection(Dir dir) {
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
    return null;
  }

}

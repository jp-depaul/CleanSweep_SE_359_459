public class CellNode {

  private int idX;
  private int ixY;
  private boolean marked = false;
  private int floorLevel;
  private int dirtLevel;

  private CellNode northCell;
  private CellNode southCell;
  private CellNode eastCell;
  private CellNode westCell;

  private CellPath northBorder;
  private CellPath southBorder;
  private CellPath eastBorder;
  private CellPath westBorder;

  public CellNode(int floorLevel, int dirtLevel) {
    this.floorLevel = floorLevel;
    this.dirtLevel = dirtLevel;
  }

  public void singlyLinkToCell(CellNode cell, Direction directionTo, CellPath path) {
    switch (directionTo) {
      case NORTH:
        northCell = cell;
        northBorder = path;
        break;
      case SOUTH:
        southCell = cell;
        southBorder = path;
        break;
      case EAST:
        eastCell = cell;
        eastBorder = path;
        break;
      case WEST:
        westCell = cell;
        westBorder = path;
    }
  }

  public void setDirtLevel(int value) { dirtLevel = value; }
  public void setFloorLevel(int value) { floorLevel = value; }

  public CellNode getAdjacent(Direction direction) {
    switch (direction) {
      case NORTH:
        return northCell;
      case SOUTH:
        return southCell;
      case EAST:
        return eastCell;
      case WEST:
        return westCell;
    }
    return null;
  }
  public CellPath getPath(Direction direction) {
    switch (direction) {
      case NORTH:
        return northBorder;
      case SOUTH:
        return southBorder;
      case EAST:
        return eastBorder;
      case WEST:
        return westBorder;
    }
    return null;
  }
  public int getDirtLevel() { return dirtLevel; }
  public int getFloorLevel() { return floorLevel; }
}

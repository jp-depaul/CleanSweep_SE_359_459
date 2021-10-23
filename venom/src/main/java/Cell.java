import java.util.HashMap;

public class Cell {
  private boolean isStation;
  private int floorLevel;
  private int dirtLevel;
  private HashMap<Dir, CellPath> path = new HashMap<Dir, CellPath>();

  public Cell(boolean isStation, int floorLevel, int dirtLevel,
      CellPath northPath, CellPath southPath, CellPath eastPath, CellPath westPath) {
    this.isStation = isStation;
    this.floorLevel = floorLevel;
    this.dirtLevel = dirtLevel;
    path.put(Dir.NORTH, northPath);
    path.put(Dir.SOUTH, southPath);
    path.put(Dir.EAST,eastPath);
    path.put(Dir.WEST, westPath);
  }

  public Cell(Cell cell) {
    this.isStation = cell.isStation;
    this.floorLevel = cell.floorLevel;
    this.dirtLevel = cell.dirtLevel;
    path.putAll(cell.path);
  }

  public boolean isStation() { return isStation; }
  public boolean needsCleaning() { return dirtLevel > 0; }
  public int getFloorLevel() { return floorLevel; }
  public CellPath getPath(Dir dir) { return path.get(dir); }
}

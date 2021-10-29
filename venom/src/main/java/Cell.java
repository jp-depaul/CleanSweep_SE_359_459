import java.util.HashMap;

public class Cell {
  public HashMap<Dir, CellBorder> border = new HashMap<Dir, CellBorder>();
  private boolean isMarked = false;
  private boolean isStation;
  private int floorLevel;
  private int dirtLevel;


  public Cell(boolean isStation, int floorLevel, int dirtLevel,
      CellBorder northPath, CellBorder southPath, CellBorder eastPath, CellBorder westPath) {
    this.isStation = isStation;
    this.floorLevel = floorLevel;
    this.dirtLevel = dirtLevel;
    border.put(Dir.NORTH, northPath);
    border.put(Dir.SOUTH, southPath);
    border.put(Dir.EAST, eastPath);
    border.put(Dir.WEST, westPath);
  }

  public Cell(Cell cell) {
    this.isStation = cell.isStation;
    this.floorLevel = cell.floorLevel;
    this.dirtLevel = cell.dirtLevel;
    border.putAll(cell.border);
  }

  public Cell() {
    border.put(Dir.NORTH, CellBorder.UNKNOWN);
    border.put(Dir.SOUTH, CellBorder.UNKNOWN);
    border.put(Dir.EAST, CellBorder.UNKNOWN);
    border.put(Dir.WEST, CellBorder.UNKNOWN);
  }

  public boolean isStation() { return isStation; }
  public boolean needsCleaning() { return dirtLevel > 0; }
  public int getCost() { return floorLevel; }
  public boolean containsUnknown() {
    return border.containsValue(CellBorder.UNKNOWN);
  }
  //public CellPath getPath(Dir dir) { return path.get(dir); }
}

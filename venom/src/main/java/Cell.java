import java.util.HashMap;

public class Cell {
  public HashMap<Dir, CellBorder> border = new HashMap<Dir, CellBorder>();
  private boolean isUnvisited;
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
    isUnvisited = false;
    this.isStation = cell.isStation;
    this.floorLevel = cell.floorLevel;
    this.dirtLevel = cell.dirtLevel;
    border.putAll(cell.border);
  }

  public Cell() {
    isUnvisited = true;
    border.put(Dir.NORTH, CellBorder.UNKNOWN);
    border.put(Dir.SOUTH, CellBorder.UNKNOWN);
    border.put(Dir.EAST, CellBorder.UNKNOWN);
    border.put(Dir.WEST, CellBorder.UNKNOWN);
  }

  public boolean isStation() {
    return isStation;
  }
  public boolean needsCleaning() {
    return dirtLevel > 0;
  }
  public int getCost() {
    return floorLevel;
  }
  public boolean containsUnknown() {
    return border.containsValue(CellBorder.UNKNOWN);
  }
  public void clean() {
    if (dirtLevel > 0) {
      dirtLevel--;
    }
  }
  public boolean isUnvisited() {
    return isUnvisited;
  }
  public boolean isMarked() {
    return isMarked;
  }
  public void setMarked(boolean value) {
    isMarked = value;
  }
}

import java.awt.Point;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.SAXException;

public class VirtualFloor {

  private Robot robot;
  private int xLength, yLength;
  private Cell[][] cell;
  private Point origin;

  public VirtualFloor(String path) throws ParserConfigurationException, IOException, SAXException {
    // load room
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(path));
    document.getDocumentElement().normalize();

    // parse floor plan
    try {
      // reused vars
      NodeList nList;
      Node node;
      Element eElement;

      // get info
      nList = document.getElementsByTagName("info");
      node = nList.item(0);
      eElement = (Element) node;
      xLength = Integer.parseInt(
          eElement.getElementsByTagName("xLength").item(0).getTextContent());
      yLength = Integer.parseInt(
          eElement.getElementsByTagName("yLength").item(0).getTextContent());
      String originCellId =
          eElement.getElementsByTagName("originId").item(0).getTextContent();

      // read cells
      cell = new Cell[xLength][yLength];
      nList = document.getElementsByTagName("cell");
      int i = 0;
      for (int y = 0; y <= yLength - 1; y++) {
        for (int x = 0; x <= xLength - 1; x++) {
          node = nList.item(i);
          eElement = (Element) node;
          if (eElement.getAttribute("id").equals(originCellId)) {
            origin = new Point(x, y);
          }
          boolean isStation = Boolean.parseBoolean(eElement.getElementsByTagName("isStation")
              .item(0).getTextContent());
          int floorLevel = Integer.parseInt(eElement.getElementsByTagName("floorLevel")
              .item(0).getTextContent());
          int dirtLevel = Integer.parseInt(eElement.getElementsByTagName("dirtLevel")
              .item(0).getTextContent());
          CellPath northPath = (CellPath.valueOf(eElement.getElementsByTagName("northPath")
              .item(0).getTextContent()));
          CellPath southPath = (CellPath.valueOf(eElement.getElementsByTagName("southPath")
              .item(0).getTextContent()));
          CellPath eastPath = (CellPath.valueOf(eElement.getElementsByTagName("eastPath")
              .item(0).getTextContent()));
          CellPath westPath = (CellPath.valueOf(eElement.getElementsByTagName("westPath")
              .item(0).getTextContent()));
          cell[x][y] = new Cell(isStation, floorLevel, dirtLevel, northPath, southPath,
              eastPath, westPath);
          i++;
        }
      }
    } catch (Exception e) {
      System.err.println("Could not parse floor plan : " + e.getClass());
    }
    // TODO: check origin is at charging station
  }

  public Robot createRobot() {
    return new Robot(this);
  }

  public Cell scanCellAtOriginRelativePos(Point relativePos) {
    // returns copy, not ref
    Point actualPos = originRelativePosToArrayPos(relativePos);
    return new Cell(cell[actualPos.x][actualPos.y]);
  }

  private Point originRelativePosToArrayPos(Point relativePos) {
    return new Point(origin.x + relativePos.x, origin.y + relativePos.y);
  }

  private Point cellIdToArrayPos(int cellId) {
    return new Point(cellId % xLength, cellId / xLength);
  }

  private int arrayPosToCellId(int x, int y) {
    return x + xLength * y;
  }

  /*
  public void setPathTypeAtCellId(int id, Dir dir, CellPath type) {
    switch (dir) {
      case NORTH:
        cell[x][y].setNorthPath(type);
        break;
      case SOUTH:
        cell[x][y].setSouthPath(type);
        break;
      case EAST:
        cell[x][y].setEastPath(type);
        break;
      case WEST:
        cell[x][y].setWestPath(type);
        break;
    }
  }
  public void setFloorLevelAtCellId(int id, int floorLevel) {
    cell[x][y].floorLevel = floorLevel;
  }
  public void setDirtLevelAtCellId(int id, int dirtLevel) {
    cell[x][y].dirtLevel = dirtLevel;
  }
  */

  public void printHomeSimple() {
    System.out.println("=========================================================================");
    System.out.println("<<Sample Printout>>");
    System.out.println("Origin: " + origin.x + "," + origin.y);
    System.out.println("CELL ID, ARRAY POSITION, PATHS (in order of north, south, east, west)");
    System.out.println("=========================================================================");


    for (int y = 0; y <= yLength - 1; y++) {
      for (int x = 0; x <= xLength - 1; x++) {
        String out = "    ID=[";
        out += arrayPosToCellId(x, y) + "] POS=[";
        out += x + "," + y + "] PATH=[";
        out += cell[x][y].getPath(Dir.NORTH) + ", ";
        out += cell[x][y].getPath(Dir.SOUTH) + ", ";
        out += cell[x][y].getPath(Dir.EAST) + ", ";
        out += cell[x][y].getPath(Dir.WEST) + "]";
        System.out.println(out);
      }
    }
    System.out.println("=========================================================================");
    System.out.println("DONE.");
    System.out.println("=========================================================================");
  }
}

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.SAXException;

public class VirtualHome {

  private Robot robot;
  private int homeLength, homeWidth;
  private CellData[][] cellArray;
  private int chargingBaseIDX, chargingBaseIDY;

  private class CellData {
    public int idX;
    public int idY;
    public int floorLevel;
    public int dirtLevel;
    public CellBorder northBorder;
    public CellBorder southBorder;
    public CellBorder eastBorder;
    public CellBorder westBorder;
  }

  public VirtualHome(String path) throws ParserConfigurationException, IOException, SAXException {
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
      homeLength = Integer.parseInt(
          eElement.getElementsByTagName("length").item(0).getTextContent());
      homeWidth = Integer.parseInt(
          eElement.getElementsByTagName("width").item(0).getTextContent());
      chargingBaseIDX = Integer.parseInt(
          eElement.getElementsByTagName("chargingBaseIDX").item(0).getTextContent());
      chargingBaseIDY = Integer.parseInt(
          eElement.getElementsByTagName("chargingBaseIDY").item(0).getTextContent());

      // get cell data
      cellArray = new CellData[homeLength][homeWidth];
      nList = document.getElementsByTagName("cell");
      int i = 0;
      for (int y = 0; y <= homeWidth - 1; y++) {
        for (int x = 0; x <= homeLength - 1; x++) {
          node = nList.item(i);
          eElement = (Element) node;
          cellArray[x][y] = new CellData();
          cellArray[x][y].floorLevel = Integer.parseInt(
              eElement.getElementsByTagName("floorLevel").item(0).getTextContent());
          cellArray[x][y].dirtLevel = Integer.parseInt(
              eElement.getElementsByTagName("dirtLevel").item(0).getTextContent());
          cellArray[x][y].northBorder = CellBorder.valueOf(
              eElement.getElementsByTagName("northBorder").item(0).getTextContent());
          cellArray[x][y].southBorder = CellBorder.valueOf(
              eElement.getElementsByTagName("southBorder").item(0).getTextContent());
          cellArray[x][y].eastBorder = CellBorder.valueOf(
              eElement.getElementsByTagName("eastBorder").item(0).getTextContent());
          cellArray[x][y].westBorder = CellBorder.valueOf(
              eElement.getElementsByTagName("westBorder").item(0).getTextContent());
          i++;
        }
      }
    } catch (Exception e) {
      System.err.println("Could not parse floor plan : " + e.getClass());
    }
  }

  public void printHomeSimple() {
    System.out.println("Charging Base: " + chargingBaseIDX + "," + chargingBaseIDY);
    System.out.println("Cell ID: [north,south,east,west]");
    for (int y = 0; y <= homeWidth - 1; y++) {
      for (int x = 0; x <= homeLength - 1; x++) {
        String out = "    ";
        out += x + "," + y + ": [";
        out += cellArray[x][y].northBorder + ", ";
        out += cellArray[x][y].southBorder + ", ";
        out += cellArray[x][y].eastBorder + ", ";
        out += cellArray[x][y].westBorder + "]";
        System.out.println(out);
      }
    }
  }
  public void changePathType(int x, int y, Direction direction, CellBorder type) {
    switch (direction) {
      case NORTH:
        cellArray[x][y].northBorder = type;
        break;
      case SOUTH:
        cellArray[x][y].southBorder = type;
        break;
      case EAST:
        cellArray[x][y].eastBorder = type;
        break;
      case WEST:
        cellArray[x][y].westBorder = type;
        break;
    }
  }
  public void changeFloorLevel(int x, int y, int floorLevel) {
    cellArray[x][y].floorLevel = floorLevel;
  }
  public void changeDirtLevel(int x, int y, int dirtLevel) {
    cellArray[x][y].dirtLevel = dirtLevel;
  }
}

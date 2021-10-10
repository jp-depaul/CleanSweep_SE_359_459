import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.SAXException;

public class VirtualHome {

  private Robot robot;
  private int homeLength, homeWidth;
  private CellData[][] cellArray;

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

      // get dimensions
      nList = document.getElementsByTagName("dimensions");
      node = nList.item(0);
      eElement = (Element) node;
      homeLength = Integer.parseInt(
          eElement.getElementsByTagName("length").item(0).getTextContent());
      homeWidth = Integer.parseInt(
          eElement.getElementsByTagName("width").item(0).getTextContent());

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
    //
    System.out.println();
  }

  public void printRoomSimple() {
    System.out.println();
  }

  public void changePathType() {

  }

  public void changeFloorLevel() {

  }
}

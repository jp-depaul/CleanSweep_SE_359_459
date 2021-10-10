import javax.xml.parsers.ParserConfigurationException;

public class Main {

  public static void main(String[] args) {

    // TODO: interpret args
    String homeFilePath = "venom/xml/VirtualRoom1.xml";

    try {
      Robot robot = new Robot(new VirtualHome(homeFilePath));
    }
    catch (Exception e) {
      System.err.println("Could not load home file : " + e.getClass());
    }

    // TODO: process commands
    while (true) {
      break;
    }

  }

  public void createMap() {}

  public void cleanMap() {}

}

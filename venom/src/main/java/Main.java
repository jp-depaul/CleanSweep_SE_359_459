import javax.xml.parsers.ParserConfigurationException;

public class Main {

  public static void main(String[] args) {
    VirtualHome virtualHome = null;
    Robot robot;

    // TODO: interpret args
    String homeFilePath = "venom/xml/VirtualHome1.xml";

    // Create robot and virtual home
    try {
      virtualHome = new VirtualHome(homeFilePath);
      robot = new Robot(virtualHome);
    }
    catch (Exception e) {
      System.err.println("Could not load home file : " + e.getClass());
    }

    // Test virtual room
    virtualHome.printHomeSimple();

    // TODO: handle commands
      // while()
  }

  public void mapFloor() {}

  public void cleanFloor() {}

}

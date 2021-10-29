public class Main {

  public static void main(String[] args) {
    VirtualFloor virtualFloor = null;
    Robot robot;

    // TODO: interpret args
    String homeFilePath = "venom/xml/FloorPlanA.xml";

    // Create virtual floor and robot
    try { virtualFloor = new VirtualFloor(homeFilePath); }
    catch (Exception e) { System.err.println("Could not load home file : " + e.getClass()); }
    robot = virtualFloor.createRobot();

    // Test floor discovery
    robot.mapFloor();

  }

}

public class Main {

  public static void main(String[] args) {
    VirtualFloor virtualFloor = null;
    Robot robot;
    String homeFilePath;

    if (args.length >= 1) {
      homeFilePath = args[0];
    }
    else {
      // Default arguments
      homeFilePath = "venom/xml/FloorPlanA.xml";
    }

    // Create virtual floor and robot
    try { virtualFloor = new VirtualFloor(homeFilePath); }
    catch (Exception e) { System.err.println("Could not load home file : " + e.getClass()); }
    robot = virtualFloor.createRobot();


    try {
      // TODO: handle commands
      robot.mapFloor();
      robot.cleanFloor();

    }
    catch(ShutdownException e) {
      System.out.println("-- ROBOT HAS SHUT DOWN --");
    }

  }

}

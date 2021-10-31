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
      homeFilePath = "venom/xml/FloorPlanB.xml";
    }

    // Create virtual floor and robot
    try { virtualFloor = new VirtualFloor(homeFilePath); }
    catch (Exception e)
    {
      System.err.println("Could not load home file : " + e.getClass());
      e.printStackTrace();
    }
    robot = virtualFloor.createRobot();

    virtualFloor.printHomeSimple(homeFilePath);


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

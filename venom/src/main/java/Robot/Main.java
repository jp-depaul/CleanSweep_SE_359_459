package Robot;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
  private VirtualFloor virtualFloor;
  private Robot robot;
  private String floorPlanPath;
  private static final String DEFAULT_FLOOR_PLAN_PATH = "venom/resources/FloorPlanB.xml";

  public Main(String floorPlanPath) {
    this.floorPlanPath = floorPlanPath;
    virtualFloor = null;
    try {
      virtualFloor = new VirtualFloor(floorPlanPath);
    } catch (Exception e) {
      System.err.println("Could not load floor plan file : " + e.getClass());
      e.printStackTrace();
    }
    robot = virtualFloor.createRobot();
  }

  public static void main(String[] args) {
    if (args != null && args.length > 0) {
     run(args[0]);
    }
    else {
      System.out.println("No floor plan provided. Default will be used (" + DEFAULT_FLOOR_PLAN_PATH + ")");
      run(DEFAULT_FLOOR_PLAN_PATH);
    }
  }

  public static void run(String floorPlanPath) {
    Main main = new Main(floorPlanPath);
    boolean loop = true;
    while (loop) {
      System.out.println("Enter a number corresponding to one of the options below:");
      System.out.println("    (1) Do cleaning");
      System.out.println("    (2) Do mapping");
      System.out.println("    (3) Check status");
      System.out.println("    (4) Quit");
      int response;
      try {
        response = Integer.parseInt(new Scanner(System.in).nextLine());
      } catch (NumberFormatException e) {
        response = -1;
      }
      switch (response) {
        case 1:
          System.out.println("Cleaning selected");
          main.doCleaning();
          break;
        case 2:
          System.out.println("Mapping selected");
          main.doMapping();
          break;
        case 3:
          System.out.println("Check status selected");
          main.robot.getStatus();
          break;
        case 4:
          loop = false;
          break;
        default:
          System.out.println("Invalid selection");
          break;
      }
    }
    System.out.println("Exiting...");
    System.exit(0);
  }
  private void doCleaning() {
    if (!robot.hasMap()) {
      System.out.println("Cannot perform cleaning until map has been made");
      return;
    }
    try {
      robot.setLogging(askDisplayActions());
      robot.cleanFloor();
    } catch (ShutdownException e) {
      shutdown();
    } catch (Exception e) {
      exitWithError(e);
    }
  }
  private void doMapping() {
    try {
      robot.setLogging(askDisplayActions());
      robot.mapFloor();
    } catch (ShutdownException e) {
      shutdown();
    } catch (Exception e) {
      exitWithError(e);
    }
  }
  private void shutdown() {
    System.err.println("Path to station obstructed - robot has shut down");
    System.exit(0);
  }
  private void exitWithError(Exception e) {
    System.err.println(e.getClass() + " at " + Arrays.toString(e.getStackTrace()));
    System.exit(1);
  }
  private boolean askDisplayActions() {
    System.out.println("Print log of robot actions? [y/n]");
    String response = new Scanner(System.in).nextLine();
    return response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes");
  }
}



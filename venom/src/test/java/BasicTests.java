import Robot.Cell;
import Robot.Robot;
import Robot.VirtualFloor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.*;
import java.util.ArrayList;

public class BasicTests {
    private static final String PATH = System.getProperty("user.dir") + "/xml/FloorPlanB.xml";
    private static Robot robot;
    private static VirtualFloor virtualFloor;

    @Test
    @BeforeAll
    @DisplayName("Initialization & Mapping Test")
    static void initialization() {

        try { virtualFloor = new VirtualFloor(PATH); }
        catch (Exception e)
        {
            System.err.println("Could not load home file : " + e.getClass());
            e.printStackTrace();
            assert(false);
        }
        robot = virtualFloor.createRobot();
        try { robot.mapFloor();}
        catch (Exception e) {
            System.err.println("Mapping test failed : " + e.getClass());
            e.printStackTrace();
            assert(false);
        }

    }

    @Test
    @DisplayName("Pathfinding Test")
    void getShortestPath() {
        robot.setPositionForTesting(new Point(-2,1));
        ArrayList<Point> expectedPath = new ArrayList<Point>();
        expectedPath.add(new Point(-2,0));
        expectedPath.add(new Point(-1,0));
        expectedPath.add(new Point(0,0));
        assertEquals(expectedPath, robot.getMinPathToPointWhere(Cell::isStation));
    }
    @Test
    @DisplayName("Path Cost Test")
    void getPathCost() {
        robot.setPositionForTesting(new Point(-2,1));
        ArrayList<Point> path = new ArrayList<Point>();
        path.add(new Point(-2,0));
        path.add(new Point(-1,0));
        path.add(new Point(0,0));
        assertEquals(3.0, robot.getPathCost(path, true));
    }
}

package Portal;

import java.io.IOException;
import java.util.Scanner;

public class CleanSweepPortal {
    private static RegisterOrLogin registration;

    public static void main(String[] args) throws IOException {

        System.out.println("Hello, user!");
        System.out.println("Welcome to the Clean Sweep Portal!");
        registration = new RegisterOrLogin();
        Robot.Main.run("venom/resources/FloorPlanB.xml");
    }


}

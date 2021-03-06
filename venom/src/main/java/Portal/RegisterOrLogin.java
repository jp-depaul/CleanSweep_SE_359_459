package Portal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class RegisterOrLogin {

    private final String MEMBERS_PATH = "venom/resources/members.txt";

    public RegisterOrLogin() throws IOException {
        System.out.println("Enter 'login' or 'create' your username:");
        Scanner loginOrCreate = new Scanner(System.in);
        String answer = loginOrCreate.nextLine();

        if (answer.equals("create")) {
            System.out.println("Please enter username you'd like to create: ");
            Scanner accountName = new Scanner(System.in);
            UserName user = new UserName();
            user.setUserName(accountName.nextLine());
            ArrayList<String> fileContents = new ArrayList<>();
            String currentLine = null;

            try {
                FileReader file = new FileReader(MEMBERS_PATH);
                BufferedReader buff = new BufferedReader(file);
                Writer output = null;
                int duplicate = 0;

                while ((currentLine = buff.readLine()) != null) {
                    fileContents.add(currentLine);
                    if (currentLine.contains(user.getUserName())) {
                        System.out.println("Username found... please enter a different username");
                        file.close();
                        duplicate = 1;
                        break;
                    }
                }
                if (duplicate == 0) {
                    System.out.println("Please provide a password for username " + user.getUserName());
                    Scanner password = new Scanner(System.in);
                    user.setPassword(password.nextLine());
                    System.out.println("Your password is: " + user.getPassword());
                    System.out.println("Please enter you product's serial number:");
                    user.setSerialNumber(password.nextLine());
                    System.out.println("Product is registered!");
                    System.out.println("When would you like the product to start cleaning? Ex. 07:00 AM ");
                    user.setScheduleStart(password.nextLine());
                    System.out.println("Your starting time is: " + user.getScheduleStart());
                    System.out.println("When would you like the product stop cleaning? Ex. 07:00 AM ");
                    user.setScheduleStop(password.nextLine());
                    System.out.println("Great! The device will clean from " + user.getScheduleStart() + " to " + user.getScheduleStop());


                    try {
                        FileWriter writing = new FileWriter(MEMBERS_PATH, true);
                        BufferedWriter textWritten = new BufferedWriter(writing);
                        BufferedReader one = new BufferedReader(file);

                        while (one.readLine() == null) {
                            textWritten.newLine();
                            textWritten.write(user.getUserName() + " , " + user.getPassword() + "\n" + user.getSerialNumber() + "\n" + user.getScheduleStart() + "\n" + user.getScheduleStop() );
                            textWritten.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (answer.equals("login")){
            System.out.println("Please enter your username");
            Scanner username = new Scanner(System.in);
            String name = username.nextLine();
            String currentLine;
            ArrayList<String> fileContents = new ArrayList<>();
            try {
                FileReader file = new FileReader(MEMBERS_PATH);
                BufferedReader buff = new BufferedReader(file);
                Writer output = null;
                String start = null;
                int i = -1;
                while ((currentLine = buff.readLine()) != null) {
                    i++;
                    fileContents.add(currentLine);
                    if (currentLine.contains(name)) {
                        System.out.println("Username found!");
                        file.close();
                        System.out.println("Enter your password");
                        String password = username.nextLine();
                        if(currentLine.contains(password)){
                            System.out.println("Login successful!");
                            String scheduleStart = Files.readAllLines(Paths.get(MEMBERS_PATH)).get(i + 2);
                            String scheduleStop = Files.readAllLines(Paths.get(MEMBERS_PATH)).get(i + 3);
                            System.out.println("\nYour current scheduled cleaning is from " + scheduleStart + " to " + scheduleStop);

                        }
                        else{ System.out.println("Incorrect password. Try again."); break;}
                        break;
                    }
                }

            } catch (FileNotFoundException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
        }
    }


}















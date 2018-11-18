import java.util.Scanner;
import java.sql.*;

public class DriverREPL {

    Connection conn;

    public DriverREPL(Connection conn) {
        this.conn = conn;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        loop: while (true) {

            System.out.println("\nPassenger, what would you like to do?");
            System.out.println("1. Take a request");
            System.out.println("2. Finish a trip");
            System.out.println("3. Check Driver rating");
            System.out.println("4. Go back");
            System.out.println("Please enter [1-4].");

            String answer = scanner.nextLine();
            Driver driver = new Driver(conn);
            int driverId;

            switch (answer) {
                case "1":
                    System.out.println("Please enter your ID.");
                    try {
                       driverId = Integer.parseInt(scanner.nextLine());
                    } catch(NumberFormatException e) {
                        System.out.println("Invalid ID.");
                        break;
                    }

                    // Checks to see if a driver is in any unfinished trips. If not, returns valid requests
                    String checkValidRequestsString = driver.checkValidRequests(driverId);
                    if (checkValidRequestsString.equals("unfinished")) {
                        System.out.println("Please finish your trip first.");
                        break;
                    }

                    System.out.println(checkValidRequestsString);

                    System.out.println("Please enter the request ID.");
                    int requestId = Integer.parseInt(scanner.nextLine());

                    System.out.println(driver.takeRequest(driverId, requestId));

                    break;
                case "2":
                    System.out.println("Please enter your ID.");
                    try {
                       driverId = Integer.parseInt(scanner.nextLine());
                    } catch(NumberFormatException e) {
                        System.out.println("Invalid ID.");
                        break;
                    }

                    String unfinishedTrip = driver.getUnfinishedTrip(driverId);

                    if (unfinishedTrip.equalsIgnoreCase("not in trip")) {
                        System.out.println("You are not on any trips");
                        break;
                    }
                    System.out.println(unfinishedTrip);
                    System.out.println("Do you wish to finish the trip? [y/n]");
                    String yesOrNo = scanner.nextLine();

                    if (yesOrNo.equalsIgnoreCase("y")) {
                        System.out.println(driver.finishTrip(driverId));
                    } else {
                        System.out.println("Okay.");
                        break;
                    }
                    break;
                case "3":
                    System.out.println("Please enter your ID.");
                    try {
                        driverId = Integer.parseInt(scanner.nextLine());
                     } catch(NumberFormatException e) {
                         System.out.println("Invalid ID.");
                         break;
                     }

                    System.out.println(driver.checkDriverRating(driverId));
                    break;
                case "4":
                    break loop;
            }
        }
    }
}

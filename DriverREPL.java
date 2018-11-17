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
                    System.out.println(driver.checkValidRequests(driverId));

                    System.out.println("Please enter the request ID.");
                    int requestId = Integer.parseInt(scanner.nextLine());
                    driver.takeRequest(driverId, requestId);

                    break;
                case "2":
                    System.out.println("Please enter your ID.");

                    break;
                case "3":
                    System.out.println("Please enter your ID.");

                    break;

                case "4":
                    break loop;
            }
        }
    }
}

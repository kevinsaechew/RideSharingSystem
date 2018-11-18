import java.util.Scanner;
import java.sql.*;

public class PassengerREPL {

    Connection conn;

    public PassengerREPL(Connection conn) {
        this.conn = conn;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        loop: while (true) {

            System.out.println("\nPassenger, what would you like to do?");
            System.out.println("1. Request a ride");
            System.out.println("2. Check trip records");
            System.out.println("3. Rate a trip");
            System.out.println("4. Go back");
            System.out.println("Please enter [1-4].");

            String answer = scanner.nextLine();
            Passenger passenger = new Passenger(conn);
            int passengerId;
            int numberOfPassengers;

            switch (answer) {
                case "1":
                    System.out.println("Please enter your ID.");
                    try {
                       passengerId = Integer.parseInt(scanner.nextLine());
                    } catch(NumberFormatException e) {
                        System.out.println("Invalid ID.");
                        break;
                    }

                    System.out.println("Please enter the number of passengers.");
                    try {
                       numberOfPassengers = Integer.parseInt(scanner.nextLine());
                    } catch(NumberFormatException e) {
                        System.out.println("Invalid number of passengers.");
                        break;
                    }

                    System.out.println("Please enter the earliest model year. (Press enter to skip)");
                    int modelYear;
                    try {
                        modelYear = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        // -1 denotes that the user has not entered a modelYear
                        modelYear = -1;
                    }
                    System.out.println("Please enter the model. (Press enter to skip)");
                    String model = scanner.nextLine();
                    if (model.trim().isEmpty()) {
                        model = "empty";
                    } else {
                        model = model.toLowerCase();
                    }
                    System.out.println(passengerId + " " + numberOfPassengers + " " + modelYear + " " + model);

                    int numVehicles;
                    if ((numVehicles = passenger.requestRide(passengerId, numberOfPassengers, modelYear, model)) != 0) {
                        String result = String.format("Your request is placed. %d drivers are able to take the request", numVehicles);
                        System.out.println(result);
                    } else {
                         System.out.println("Please adjust request criteria");
                    }
                    break;
                case "2":
                    System.out.println("Please enter your ID.");
                    passengerId = Integer.parseInt(scanner.nextLine());

                    System.out.println("Please enter the start date.");
                    Date startDate = java.sql.Date.valueOf(scanner.nextLine());
                    Timestamp startTimestamp = new Timestamp(startDate.getTime());  

                    System.out.println("Please enter the end date.");
                    Date endDate = java.sql.Date.valueOf(scanner.nextLine());
                    Timestamp endTimestamp = new Timestamp(endDate.getTime());  

                    System.out.println(passenger.checkTripRecords(passengerId, startTimestamp, endTimestamp));

                    break;
                case "3":
                    System.out.println("Please enter your ID.");
                    passengerId = Integer.parseInt(scanner.nextLine());

                    System.out.println("Please enter your trip ID.");
                    int tripID = Integer.parseInt(scanner.nextLine());

                    System.out.println("Please enter the rating.");
                    int rating = Integer.parseInt(scanner.nextLine());
                    while (rating < 1 || rating > 5) {
                        System.out.println("Please enter a rating between 1 and 5 (or enter to exit)");
                        try {
                            rating = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            rating = -1;
                            break;
                        } 

                    }

                    if (rating != -1) {
                        if (passenger.isTripIsValid(passengerId, tripID, rating)) {
                            System.out.println(passenger.rateTrip(passengerId, tripID, rating));
                        } else {
                            System.out.println("Not a valid trip to rate.");
                        }
                    }
                    break;

                case "4":
                    break loop;
            }
        }
    }
}

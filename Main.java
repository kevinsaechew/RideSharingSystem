import java.util.Scanner;
import java.sql.*;
public class Main {

    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db10";
    public static String dbUsername = "Group10";
    public static String dbPassword = "HI_KEVIN";

    private static Connection connectToOracle(){
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch (ClassNotFoundException e){
            System.out.println("[Error]: Java MySQL DB Driver not found!!");
            System.exit(0);
        } catch (SQLException e){
            //System.out.println(e);
        }
        return conn;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the mission design system!");
        System.out.println();

        while (true){
            try {
                Connection conn = connectToOracle();
                if (conn == null) {
                    System.out.println("[Error]: Database connection failed, system exit.");
                } else {

                    System.out.println("Are you an administrator, passenger, or driver?");
                    System.out.println("1. Administrator");
                    System.out.println("2. Passenger");
                    System.out.println("3. Driver");
                    System.out.println("4. Exit the program");
                    System.out.println("Please enter [1-4].");

                    String answer = scanner.nextLine();

                    switch (answer) {
                        case "1":
                            AdministratorREPL adminREPL = new AdministratorREPL(conn);
                            adminREPL.run();
                            continue;
                        case "2":
                            PassengerREPL passengerREPL = new PassengerREPL(conn);
                            passengerREPL.run();
                            continue;
                        case "3":
                            DriverREPL driverREPL = new DriverREPL(conn);
                            driverREPL.run();
                            continue;
                        case "4":
                            break;
                        default:
                            continue;
                    }
                }
                break;

            }catch (Exception e){
                System.out.print("[Error]: ");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
        System.exit(0);

    }
}
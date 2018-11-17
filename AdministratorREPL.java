import java.util.Scanner;
import java.sql.*;

public class AdministratorREPL {

    Connection conn;

    public AdministratorREPL(Connection conn) {
        this.conn = conn;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        loop: while (true) {

            System.out.println("\nAdministrator, what would you like to do?");
            System.out.println("1. Create tables");
            System.out.println("2. Delete tables");
            System.out.println("3. Load tables");
            System.out.println("4. Check tables");
            System.out.println("5. Go back");
            System.out.println("Please enter [1-5].");

            String answer = scanner.nextLine();
            Administrator admin = new Administrator(conn);

            switch (answer) {
                case "1":
                    admin.createTables();
                    System.out.println("Tables have been created.\n");
                    break;
                case "2":
                    admin.deleteTables();
                    System.out.println("Tables have been deleted.\n");
                    break;
                case "3":
                    System.out.println("Please enter the folder path.");
                    String folderPath = scanner.nextLine();
                    admin.loadTables(folderPath);
                    break;
                case "4":
                    System.out.print(admin.checkTables());
                    break;
                case "5":
                    break loop;
            }
        }
    }
}

import java.io.*;
import java.math.*;
import java.sql.*;

public class Loader {

    String folderPath;
    Connection conn;

    public Loader(Connection conn, String folderPath) {
        this.conn = conn;
        this.folderPath = folderPath;
    }

    private void loadTable(String path, Executor formatter) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                formatter.execute(this.conn, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTables() {

        String driverPath = this.folderPath + "/drivers.csv";
        String passengerPath = this.folderPath + "/passengers.csv";
        String tripPath = this.folderPath + "/trips.csv";
        String vehiclePath = this.folderPath + "/vehicles.csv";

        Executor driverExecutor = (Connection conn, String[] values) -> {
            String sql = "INSERT INTO driver (id, name, vehicle_id)"
                    + " VALUES (?, ?, ?);";
            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                prep.setInt(1, Integer.parseInt(values[0]));
                prep.setString(2, values[1]);
                prep.setString(3, values[2]);
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        Executor passengerExecutor = (Connection conn, String[] values) -> {
            String sql = "INSERT INTO passenger (id, name)"
                    + " VALUES (?, ?);";
            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                prep.setInt(1, Integer.parseInt(values[0]));
                prep.setString(2, values[1]);
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        Executor tripExecutor = (Connection conn, String[] values) -> {
            String sql = "INSERT INTO trip (id, driver_id, passenger_id, start, end, fee, rating)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                prep.setInt(1, Integer.parseInt(values[0]));
                prep.setInt(2, Integer.parseInt(values[1]));
                prep.setInt(3, Integer.parseInt(values[2]));
                prep.setTimestamp(4, java.sql.Timestamp.valueOf(values[3]));
                prep.setTimestamp(5, java.sql.Timestamp.valueOf(values[4]));
                prep.setBigDecimal(6, new BigDecimal(values[5]));
                prep.setBigDecimal(7, new BigDecimal(values[6]));
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        Executor vehicleExecutor = (Connection conn, String[] values) -> {
            String sql = "INSERT INTO vehicle (id, model, model_year, seats)"
                    + " VALUES (?, ?, ?, ?);";
            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                prep.setString(1, values[0]);
                prep.setString(2, values[1]);
                prep.setInt(3, Integer.parseInt(values[2]));
                prep.setInt(4, Integer.parseInt(values[3]));
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        this.loadTable(vehiclePath, vehicleExecutor);
        this.loadTable(driverPath, driverExecutor);
        this.loadTable(passengerPath, passengerExecutor);
        this.loadTable(tripPath, tripExecutor);
    }

    interface Executor {
        void execute(Connection conn, String[] values);
    }
}

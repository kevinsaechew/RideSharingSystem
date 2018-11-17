import java.sql.*;

public class Administrator {

    Connection conn;

    public Administrator(Connection conn) {
        this.conn = conn;
    }

    public void createTables() {
        String driverSQL = "CREATE TABLE IF NOT EXISTS driver("
            + "id integer primary key,"
            + "name varchar(30),"
            + "vehicle_id varchar(6),"
            + "FOREIGN KEY (vehicle_id) REFERENCES vehicle(id));";

        String vehicleSQL = "CREATE TABLE IF NOT EXISTS vehicle("
            + "id varchar(6) primary key,"
            + "model varchar(30),"
            + "model_year integer,"
            + "seats integer);";

        String passengerSQL = "CREATE TABLE IF NOT EXISTS passenger("
            + "id integer primary key,"
            + "name varchar(30));";

        String requestSQL = "CREATE TABLE IF NOT EXISTS request("
            + "id integer primary key,"
            + "passenger_id integer,"
            + "model_year integer,"
            + "model varchar(30),"
            + "passengers integer,"
            + "taken boolean,"
            + "FOREIGN KEY (passenger_id) REFERENCES passenger(id));";

        String tripSQL = "CREATE TABLE IF NOT EXISTS trip("
            + "id integer primary key,"
            + "driver_id integer,"
            + "passenger_id integer,"
            + "start datetime,"
            + "end datetime,"
            + "fee decimal,"
            + "rating decimal,"
            + "FOREIGN KEY (driver_id) REFERENCES driver(id),"
            + "FOREIGN KEY (passenger_id) REFERENCES passenger(id));";

        String[] sqls = {vehicleSQL, passengerSQL, requestSQL, driverSQL, tripSQL};
        for (String sql : sqls) {
            try (PreparedStatement prep = conn.prepareStatement(sql);) {
                prep.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.print("Processing... Done! Tables are created!\n");
    }

    public void deleteTables() {
        String str = "DROP TABLE IF EXISTS ";
        String[] tableNames = {"trip", "request", "passenger", "driver", "vehicle"};

        for (String tableName : tableNames) {
            try (PreparedStatement prep = conn.prepareStatement(str + tableName + ";")) {
                prep.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadTables(String filePath) {
        String[] tableNames = {"trip", "request", "passenger", "driver", "vehicle"};
        for (String tableName: tableNames) {
            if (!tableExists(tableName)) {
                System.out.println("Table '" + tableName + "' doesn't exist."
                    + " You must create all tables first before loading data.");
                return;
            }
        }
        Loader loader = new Loader(conn, filePath);
        loader.loadTables();
        System.out.print("Data is loaded.\n");
    }

    private boolean tableExists(String tableName) {
        String sql = "SELECT count(*) FROM information_schema.TABLES "
                + "WHERE (TABLE_SCHEMA = 'db10') "
                + "AND (TABLE_NAME = '" + tableName + "');";

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    int count = Integer.parseInt(rs.getString("count(*)"));
                    return count == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String checkTables() {
        String str = "Number of records in each table:\n";
        String[] tableNames = {"driver", "passenger", "request", "trip", "vehicle"};

        for (String tableName : tableNames) {
            if (!tableExists(tableName)) {
                str += tableName + ": " + "This table doesn't exist.\n";
                continue;
            }

            String sql = String.format("SELECT COUNT(*) FROM %s;", tableName);
            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                try (ResultSet rs = prep.executeQuery()) {
                    while (rs.next()) {
                        str += tableName + ": " + rs.getString("count(*)") + "\n";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return str;
    }

}
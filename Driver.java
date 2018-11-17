import java.sql.*;

public class Driver {

    Connection conn;

    public Driver(Connection conn) {
        this.conn = conn;
    }

    public String checkValidRequests(int driverId) {
        String str = "Request ID, Passenger Name, Passengers \n";

        String sqlRequest = "SELECT request.id, passenger.name, request.passengers" +
        " FROM driver, request, passenger, vehicle WHERE driver.id = ? AND driver.vehicle_id = vehicle.id AND passenger.id = request.passenger_id " +
        "AND vehicle.seats > request.passengers AND vehicle.model_year <= request.model_year AND vehicle.model LIKE CONCAT(\'%%\', request.model, \'%%\')";
        try (PreparedStatement prep = conn.prepareStatement(sqlRequest)) {
            prep.setInt(1, driverId);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    str += rs.getInt("request.id") + ", " + rs.getString("passenger.name") + rs.getInt("request.passengers") + "\n";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
        
    }

    public String takeRequest(int driverId, int requestId) {

        return null;
    }


}
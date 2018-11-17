import java.sql.*;

public class Passenger {

    Connection conn;

    public Passenger(Connection conn) {
        this.conn = conn;
    }

    public int requestRide(int passengerId, int numberOfPassengers, int modelYear, String model) {
        String sql = null;
        int numberOfRides = 0;

        if (modelYear == -1 && model == "empty") {
            sql = String.format("SELECT COUNT(*) FROM vehicle WHERE seats >= %d ", numberOfPassengers);
        } else if (modelYear != -1 && model == "empty") {
            sql = String.format("SELECT COUNT(*) FROM vehicle WHERE seats >= %d AND model_year >= %d ", numberOfPassengers, modelYear);
        } else {
            sql = String.format("SELECT COUNT(*) FROM vehicle WHERE seats >= %d AND model_year >= %d AND LOWER(model) LIKE \'%%%s%%\'", numberOfPassengers, modelYear, model);
        }

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    numberOfRides = rs.getInt("count(*)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // numberOfRides is the number of vehicles that satisfy the criteria
        if (numberOfRides != 0){
            String sqlUniqueId = "SELECT MAX(id) AS max_id FROM request;";
            int uniqueRequestId = 0;
            try (PreparedStatement prep = conn.prepareStatement(sqlUniqueId)) {
                try (ResultSet rs = prep.executeQuery()) {
                    if (rs.next()) {
                        // This gets a new and unused request ID
                        uniqueRequestId = rs.getInt("max_id") + 1;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

                String sqlRequest = "INSERT INTO request (id, passenger_id, model_year, model, passengers, taken)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement prep = conn.prepareStatement(sqlRequest)) {
                prep.setInt(1, uniqueRequestId);
                prep.setInt(2, passengerId);
                prep.setInt(3, modelYear);
                prep.setString(4, model);
                prep.setInt(5, numberOfPassengers);
                prep.setBoolean(6, false);
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
        return numberOfRides;
    }

    // Done
    public String checkTripRecords(int passengerId, Timestamp startTimestamp, Timestamp endTimestamp) {
        String str = "Trip ID, Driver Name, Vehicle ID, Vehicle model, Start, End, Fee, Rating \n";

        String sqlRequest = "SELECT trip.id, driver.name, vehicle.id, vehicle.model, trip.start, trip.end, trip.fee, trip.rating" +
        " FROM trip, driver, vehicle WHERE trip.passenger_id = ? AND trip.start >= ? AND trip.end <= ? AND trip.driver_id = driver.id AND driver.vehicle_id = vehicle.id;";
        try (PreparedStatement prep = conn.prepareStatement(sqlRequest)) {
            prep.setInt(1, passengerId);
            prep.setTimestamp(2, startTimestamp);
            prep.setTimestamp(3, endTimestamp);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    str += rs.getInt("trip.id") + ", " + rs.getString("driver.name")  + ", " + rs.getInt("vehicle.id")+ 
                    ", " + rs.getString("vehicle.model")+ ", " + rs.getTimestamp("trip.start") + ", " + rs.getTimestamp("trip.end") +   
                    ", " + rs.getInt("trip.fee")   + ", " + rs.getInt("trip.rating") + "\n";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;

    }
    public String rateTrip(int passengerId, int tripId, int rating ) {
        String str = "Trip ID, Driver Name, Vehicle ID, Vehicle model, Start, End, Fee, Rating \n";
        String sqlUpdate = String.format("UPDATE trip SET rating = %d WHERE trip.id = %d AND trip.passenger_id = %d", rating, tripId, passengerId);

        try (Statement updateStatement = conn.createStatement()) {
        updateStatement.execute(sqlUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlRequest = "SELECT trip.id, driver.name, vehicle.id, vehicle.model, trip.start, trip.end, trip.fee, trip.rating" +
        " FROM trip, driver, vehicle WHERE trip.passenger_id = ? AND trip.id = ? AND trip.driver_id = driver.id AND driver.vehicle_id = vehicle.id;";

        try (PreparedStatement prep = conn.prepareStatement(sqlRequest)) {
            prep.setInt(1, passengerId);
            prep.setInt(2, tripId);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    str += rs.getInt("trip.id") + ", " + rs.getString("driver.name")  + ", " + rs.getString("vehicle.id")+ 
                    ", " + rs.getString("vehicle.model")+ ", " + rs.getTimestamp("trip.start") + ", " + rs.getTimestamp("trip.end") +   
                    ", " + rs.getInt("trip.fee")   + ", " + rs.getInt("trip.rating") + "\n";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;

    }


}

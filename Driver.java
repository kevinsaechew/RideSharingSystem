import java.sql.*;
import java.text.DateFormat;


public class Driver {

    Connection conn;

    public Driver(Connection conn) {
        this.conn = conn;
    }

    public String checkValidRequests(int driverId) {
        String str = "Request ID, Passenger Name, Passengers \n";

        String sqlCheckUnfinishedTrip = String.format("SELECT * FROM trip WHERE trip.driver_id = %d;", driverId);

        try (PreparedStatement prep = conn.prepareStatement(sqlCheckUnfinishedTrip)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    return "unfinished";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        String str = "Trip ID, Driver Name, Vehicle ID, Vehicle model, Start, End, Fee, Rating \n";
        String sqlUpdate = String.format("UPDATE request SET taken = 1 WHERE request.id = %d", requestId);

        // Marks request as taken
        try (Statement updateStatement = conn.createStatement()) {
        updateStatement.execute(sqlUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Gets unique TripID
        String sqlUniqueId = "SELECT MAX(id) AS max_id FROM trip;";
        int uniqueTripId = 0;
        try (PreparedStatement prep = conn.prepareStatement(sqlUniqueId)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    // This gets a new and unused request ID
                    uniqueTripId = rs.getInt("max_id") + 1;
                    System.out.println(uniqueTripId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String passengerName = null;
        int passengerId = 0;

        // Gets passengerName and passengerId 
        String sqlGetPassengerInfo = String.format("SELECT passenger.name, passenger.id FROM passenger, request WHERE request.id = %d"
                                    + "AND passenger.id = request.passenger_id FROM trip;", requestId);

        try (PreparedStatement prep = conn.prepareStatement(sqlGetPassengerInfo)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    passengerName = rs.getString("passenger.name");
                    passengerId = rs.getInt("passenger.id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlMakeTrip = "INSERT INTO trip (id, driver_id, passenger_id, start, end, fee, rating))"
        + " VALUES (?, ?, ?, ?, ?, ?, ?);";

        Timestamp startTime = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement prep = conn.prepareStatement(sqlMakeTrip)) {
            prep.setInt(1, uniqueTripId);
            prep.setInt(2, driverId);
            prep.setInt(3, passengerId);
            prep.setTimestamp(4, startTime);
            prep.setNull(5, Types.TIMESTAMP);
            prep.setNull(6, Types.DECIMAL);
            prep.setNull(7, Types.DECIMAL);
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        str += String.format("%d, %s, %d \n", uniqueTripId, passengerName, startTime);
        return str;
    }

    public String getUnfinishedTrip(int driverId) {
        String str = "Trip ID, Passenger ID, Start \n";

        String sqlGetUnfinishedTrips = String.format("SELECT trip.id, trip.passenger_id, start FROM trip WHERE trip.driver_id = %d AND end IS NULL;", driverId);

        try (PreparedStatement prep = conn.prepareStatement(sqlGetUnfinishedTrips)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    str += rs.getInt("trip.id") + ", " + rs.getInt("trip.passenger_id") + rs.getTimestamp("trip.start") + "\n";
                } else {
                    return "not in trip";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return str;
    }

    public String finishTrip(int driverId) {
        String str = "Trip ID, Passenger name, Start, End, Fee \n";

        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        int tripId = 0;
        Timestamp startTime = null;
        String passengerName = null;

        String sqlGetParameters = String.format("SELECT trip.id, passenger.name, trip.start FROM trip, driver, passenger WHERE"
                         + "driver.id = %d AND trip.passenger_id = passenger.id AND trip.driver_id = driver.id AND end IS NULL", driverId);

        try (PreparedStatement prep = conn.prepareStatement(sqlGetParameters)) {
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    tripId = rs.getInt("trip.id");
                    startTime = rs.getTimestamp("trip.start");
                    passengerName = rs.getString("passenger.name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();      
        }
        
        long differenceInMilliseconds = Math.abs(endTime.getTime() - startTime.getTime());

        // Converts milliseconds into seconds and then divides by 60 to get number of minutes 
        int fee = (int) Math.round(differenceInMilliseconds / (1000.0 * 60.0));

        String sqlUpdate = "UPDATE trip SET end = ?, fee = ? WHERE trip.id = ?";

        try (PreparedStatement prep = conn.prepareStatement(sqlUpdate)) {
            prep.setTimestamp(1, endTime);
            prep.setInt(2, fee);
            prep.setInt(3, tripId);
            prep.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        str += tripId + ", " + passengerName + ", " + startTime + ", " + endTime + ", " + fee + "\n";
        return str;
    }
    public String checkDriverRating(int driverId) {

        String sql = String.format("SELECT COUNT(*) FROM trip WHERE trip.driver_id = %d AND rating IS NOT NULL ", driverId);
        int numberOfRatings = 0;
        double rating = 0.0;
        double counter = 0.0;

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    numberOfRatings = rs.getInt("count(*)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (numberOfRatings < 5) {
            return "Your rating is not yet determined.";
        } 

        String sqlRatings = String.format("SELECT rating FROM trip WHERE driver_id = %d AND rating IS NOT NULL ", driverId);

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    rating += rs.getInt("rating");
                    counter++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Will round to two decimal places
        rating = Math.round((rating / counter * 100.00)) / 100.00;

        return "Your driver rating is " + rating;
    }
    
}
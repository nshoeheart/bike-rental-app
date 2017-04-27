package clients;

import util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalManagerClient {

    public BikeRentalManagerClient() {}

    public void run() {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();

            //todo insert manager client code here
        } catch (SQLException e) {
            //print error?
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

package clients;

import util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalCustomerClient {

    private String name;

    public BikeRentalCustomerClient(String name) {
        this.name = name;
    }

    public void run() {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();

            //todo insert customer client code here
        } catch (SQLException e) {
            //print error?
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

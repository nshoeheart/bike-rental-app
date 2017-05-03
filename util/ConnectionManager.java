package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Nathan on 4/26/17.
 */
public class ConnectionManager {

    public static Connection getConnection() throws SQLException {
        // Get HawkID and password from system environment variables - these will need to be set on whoever's machine this application is running on
        final String HW7B_DB_USER = System.getenv("HW7B_DB_USER");
        final String HW7B_DB_PASS = System.getenv("HW7B_DB_PASS");

        // Define route to database
        // NOTE: You must be connected to UIowa VPN for the connection to work (even if you are on campus internet)
        final String MYSQL_SERVER_ROUTE = "jdbc:mysql://dbdev.divms.uiowa.edu:3306/db_" + HW7B_DB_USER;

        System.out.println("Attempting to connect to database...");
        Connection connection = DriverManager.getConnection(MYSQL_SERVER_ROUTE, HW7B_DB_USER, HW7B_DB_PASS);
        System.out.println("Connected to database successfully\n");

        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        connection.setAutoCommit(false);

        return connection;
    }

    public static void rollbackConnection(Connection connection) {
        if (connection != null) {
            //System.out.println("Rolling back transaction for this connection...");

            try {
                connection.rollback();
                //System.out.println("Successfully performed rollback for this connection\n");
            } catch (SQLException e) {
                //System.out.println("Failed to perform a rollback for this connection\n");
            }
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            System.out.println("Closing database connection...");

            try {
                connection.close();
                System.out.println("Database connection closed successfully\n");
            } catch (SQLException e) {
                System.out.println("Failed to close connection\n");
            }
        }
    }
}

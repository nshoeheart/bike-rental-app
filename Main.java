import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Nathan on 4/17/17.
 */
public class Main {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = getConnection();
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }

    private static Connection getConnection() throws SQLException {
        // Get HawkID and password from system environment variables - these will need to be set on whoever's machine this application is running on
        final String HW7B_DB_USER = System.getenv("HW7B_DB_USER");
        final String HW7B_DB_PASS = System.getenv("HW7B_DB_PASS");

        // Define route to database
        // NOTE: You must be connected to UIowa VPN for the connection to work (even if you are on campus internet)
        final String MYSQL_SERVER_ROUTE = "jdbc:mysql://dbdev.divms.uiowa.edu:3306/db_" + HW7B_DB_USER;

        System.out.println("Attempting to connect to database...");
//        System.out.println("\tRoute: " + MYSQL_SERVER_ROUTE);
//        System.out.println("\tUser:  " + HW7B_DB_USER);
//        System.out.println("\tPass:  " + HW7B_DB_PASS);

        Connection connection = DriverManager.getConnection(MYSQL_SERVER_ROUTE, HW7B_DB_USER, HW7B_DB_PASS);

        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        connection.setAutoCommit(false);

        return connection;
    }

    private static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                System.out.println("Failed to close connection");
            }
        }
    }
}

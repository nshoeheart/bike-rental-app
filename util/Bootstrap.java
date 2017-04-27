package util;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Nathan on 4/26/17.
 */
public class Bootstrap {

    public static void bootstrapDatabase() throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        try {
            System.out.println("Creating database tables...");
            executeSQLScript("sql/create-db.sql", connection);
            System.out.println("Database tables created successfully");

            System.out.println("Populating tables with initial data...");
            executeSQLScript("sql/bootstrap-data.sql", connection);
            System.out.println("Tables populated with initial data successfully");
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    private static void executeSQLScript(String fileName, Connection connection) throws SQLException {
        try {
            new ScriptRunner(connection).runScript(new BufferedReader(new FileReader(fileName)));
        } catch (FileNotFoundException e) {
            throw new SQLException("SQL Script not found for file: " + fileName);
        }
    }
}

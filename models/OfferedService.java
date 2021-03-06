package models;

import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/17/17.
 */
public class OfferedService {
    public int id;
    public String name;
    public float cost;

    private OfferedService(int id, String name, float cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public static OfferedService createNewOfferedService(Connection connection, String name, float cost) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OfferedService (name, cost) VALUES (?, ?)");
        preparedStatement.setString(1, name);
        preparedStatement.setFloat(2, cost);
        int result = preparedStatement.executeUpdate();

        if (result == 1) { // new OfferedService successfully inserted
            ResultSet offeredServiceRS =  connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id");
            if (offeredServiceRS.next()) {
                return getById(connection, offeredServiceRS.getInt(1));
            } else {
                throw new SQLException("Unable to find new OfferedService in database");
            }
        } else { // OfferedService not inserted
            throw new SQLException("Unable to create new OfferedService");
        }
    }

    public static OfferedService getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM OfferedService os WHERE os.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return createFromResultSetRow(resultSet);
            } else {
                throw new SQLException("OfferedService with id: " + id + " not found");
            }
        } finally {
            ConnectionManager.closePreparedStatement(preparedStatement);
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static List<OfferedService> createListFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            List<OfferedService> offeredServices = new ArrayList<>();

            while (resultSet.next()) {
                offeredServices.add(createFromResultSetRow(resultSet));
            }

            return offeredServices;
        } finally {
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    private static OfferedService createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        float cost = resultSet.getFloat("cost");

        return new OfferedService(id, name, cost);
    }

    public static void printOfferedServiceDetails(List<OfferedService> offeredServices) {
        System.out.println("Service ID\tService Name\tService Cost");
        for (OfferedService offeredService : offeredServices) {
            System.out.println(String.format("%s\t\t%s\t%s", offeredService.id, offeredService.name, offeredService.cost));
        }
    }
}

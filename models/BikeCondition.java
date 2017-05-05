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
public class BikeCondition {
    public int id;
    public String name;

    private BikeCondition(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BikeCondition createNewBikeCondition(Connection connection, String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BikeCondition (name) VALUES (?)");
        preparedStatement.setString(1, name);
        int result = preparedStatement.executeUpdate();

        if (result == 1) { // new BikeCondition successfully inserted
            ResultSet bikeCondRS =  connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id");
            if (bikeCondRS.next()) {
                return getById(connection, bikeCondRS.getInt(1));
            } else {
                throw new SQLException("Unable to find new BikeCondition in database");
            }
        } else { // BikeCondition not inserted
            throw new SQLException("Unable to create new BikeCondition");
        }
    }

    public static BikeCondition getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM BikeCondition bc WHERE bc.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return createFromResultSetRow(resultSet);
            } else {
                throw new SQLException("BikeCondition with id: " + id + " not found");
            }
        } finally {
            ConnectionManager.closePreparedStatement(preparedStatement);
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static List<BikeCondition> createListFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            List<BikeCondition> bikeConditions = new ArrayList<>();

            while (resultSet.next()) {
                bikeConditions.add(createFromResultSetRow(resultSet));
            }

            return bikeConditions;
        } finally {
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static BikeCondition createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        return new BikeCondition(id, name);
    }
}

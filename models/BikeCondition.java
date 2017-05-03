package models;

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

    public static BikeCondition getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BikeCondition bc WHERE bc.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("BikeCondition with id: " + id + " not found");
        }
    }

    public static List<BikeCondition> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<BikeCondition> bikeConditions = new ArrayList<>();

        while (resultSet.next()) {
            bikeConditions.add(createFromResultSetRow(resultSet));
        }

        return bikeConditions;
    }

    public static BikeCondition createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        return new BikeCondition(id, name);
    }
}

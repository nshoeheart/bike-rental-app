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
public class OfferedService {
    public int id;
    public String name;
    public float cost;

    private OfferedService(int id, String name, float cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public static OfferedService getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OfferedService os WHERE os.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("OfferedService with id: " + id + " not found");
        }
    }

    public static List<OfferedService> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<OfferedService> offeredServices = new ArrayList<>();

        while (resultSet.next()) {
            offeredServices.add(createFromResultSetRow(resultSet));
        }

        return offeredServices;
    }

    public static OfferedService createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        float cost = resultSet.getFloat("cost");

        return new OfferedService(id, name, cost);
    }
}

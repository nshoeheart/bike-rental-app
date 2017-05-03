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
public class Equipment {
    public int id;
    public String brand;
    public String name;
    public float cost;
    public int stock;

    private Equipment(int id, String brand, String name, float cost, int stock) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }

    public static Equipment getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Equipment e WHERE e.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("Equipment with id: " + id + " not found");
        }
    }

    public static List<Equipment> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<Equipment> equipment = new ArrayList<>();

        while (resultSet.next()) {
            equipment.add(createFromResultSetRow(resultSet));
        }

        return equipment;
    }

    public static Equipment createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String brand = resultSet.getString("brand");
        String name = resultSet.getString("name");
        float cost = resultSet.getFloat("cost");
        int stock = resultSet.getInt("stock");

        return new Equipment(id, brand, name, cost, stock);
    }
}

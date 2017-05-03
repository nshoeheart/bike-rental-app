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
public class Customer {
    public int id;
    public String lastName;
    public String firstName;

    private Customer(int id, String lastName, String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public static Customer createNewCustomer(Connection connection, String lastName, String firstName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer (last_name, first_name) VALUES (?, ?)");
        preparedStatement.setString(1, lastName);
        preparedStatement.setString(2, firstName);
        int result = preparedStatement.executeUpdate();

        if (result == 1) { // new Customer successfully inserted
            int newCustomerId = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id").getInt("id");
            return getById(connection, newCustomerId);
        } else { // Customer not inserted
            throw new SQLException("Unable to create new customer");
        }
    }

    public static Customer getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Customer c WHERE c.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("Customer with id: " + id + " not found");
        }
    }

    public static List<Customer> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<Customer> customers = new ArrayList<>();

        while (resultSet.next()) {
            customers.add(createFromResultSetRow(resultSet));
        }

        return customers;
    }

    public static Customer createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String lastName = resultSet.getString("last_name");
        String firstName = resultSet.getString("first_name");

        return new Customer(id, lastName, firstName);
    }
}

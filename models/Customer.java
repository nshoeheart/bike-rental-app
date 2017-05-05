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
            ResultSet customerRS =  connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id");
            if (customerRS.next()) {
                return getById(connection, customerRS.getInt(1));
            } else {
                throw new SQLException("Unable to find new Customer in database");
            }
        } else { // Customer not inserted
            throw new SQLException("Unable to create new Customer");
        }
    }

    public static Customer getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM Customer c WHERE c.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return createFromResultSetRow(resultSet);
            } else {
                throw new SQLException("Customer with id: " + id + " not found");
            }
        } finally {
            ConnectionManager.closePreparedStatement(preparedStatement);
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static List<Customer> createListFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            List<Customer> customers = new ArrayList<>();

            while (resultSet.next()) {
                customers.add(createFromResultSetRow(resultSet));
            }

            return customers;
        } finally {
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    private static Customer createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String lastName = resultSet.getString("last_name");
        String firstName = resultSet.getString("first_name");

        return new Customer(id, lastName, firstName);
    }
}

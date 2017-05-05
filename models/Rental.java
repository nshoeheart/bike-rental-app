package models;

import util.ConnectionManager;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/17/17.
 */
public class Rental {
    public int id;
    public int bikeId;
    public int customerId;
    public LocalDate checkoutDate;
    public LocalDate dueDate;
    public LocalDate returnDate;
    public boolean checkedOut;

    private Rental(int id, int bikeId, int customerId, LocalDate checkoutDate, LocalDate dueDate, LocalDate returnDate, boolean checkedOut) {
        this.id = id;
        this.bikeId = bikeId;
        this.customerId = customerId;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.checkedOut = checkedOut;
    }

    public Bicycle getBicycle(Connection connection) throws SQLException {
        return Bicycle.getById(connection, bikeId);
    }

    public Customer getCustomer(Connection connection) throws SQLException {
        return Customer.getById(connection, customerId);
    }

    public static Rental createNewRental(Connection connection, int bikeId, int customerId, LocalDate checkoutDate, LocalDate dueDate, LocalDate returnDate, boolean checkedOut) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Rental (bike_id, customer_id, checkout_date, due_date, return_date, checkout_out) VALUES (?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, bikeId);
        preparedStatement.setInt(2, customerId);
        preparedStatement.setDate(3, Date.valueOf(checkoutDate));
        preparedStatement.setDate(4, Date.valueOf(dueDate));
        preparedStatement.setDate(5, (returnDate == null ? null : Date.valueOf(returnDate)));
        preparedStatement.setBoolean(6, checkedOut);
        int result = preparedStatement.executeUpdate();

        if (result == 1) { // new Rental successfully inserted
            ResultSet rentalRS =  connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id");
            if (rentalRS.next()) {
                return getById(connection, rentalRS.getInt(1));
            } else {
                throw new SQLException("Unable to find new Rental in database");
            }
        } else { // Rental not inserted
            throw new SQLException("Unable to create new Rental");
        }
    }

    public static Rental getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return createFromResultSetRow(resultSet);
            } else {
                throw new SQLException("Rental with id: " + id + " not found");
            }
        } finally {
            ConnectionManager.closePreparedStatement(preparedStatement);
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static List<Rental> createListFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            List<Rental> rentals = new ArrayList<>();

            while (resultSet.next()) {
                rentals.add(createFromResultSetRow(resultSet));
            }

            return rentals;
        } finally {
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    public static Rental createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int bikeId = resultSet.getInt("bike_id");
        int customerId = resultSet.getInt("customer_id");
        LocalDate checkoutDate = Instant.ofEpochMilli(resultSet.getDate("checkout_date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate
        LocalDate dueDate = Instant.ofEpochMilli(resultSet.getDate("due_date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate
        LocalDate returnDate;

        try {
            Date returnDateSql = resultSet.getDate("return_date");
            returnDate = (returnDateSql == null ? null : Instant.ofEpochMilli(returnDateSql.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()); // convert from Date to LocalDate
        } catch (SQLException e) {
            returnDate = null;
        }
        boolean checkedOut = resultSet.getBoolean("checked_out");

        return new Rental(id, bikeId, customerId, checkoutDate, dueDate, returnDate, checkedOut);
    }
}

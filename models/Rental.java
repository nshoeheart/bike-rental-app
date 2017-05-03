package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static Rental getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("Rental with id: " + id + " not found");
        }
    }

    public static List<Rental> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<Rental> rentals = new ArrayList<>();

        while (resultSet.next()) {
            rentals.add(createFromResultSetRow(resultSet));
        }

        return rentals;
    }

    public static Rental createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int bikeId = resultSet.getInt("bike_id");
        int customerId = resultSet.getInt("customer_id");
        LocalDate checkoutDate = Instant.ofEpochMilli(resultSet.getDate("checkout_date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate
        LocalDate dueDate = Instant.ofEpochMilli(resultSet.getDate("due_date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate
        LocalDate returnDate = Instant.ofEpochMilli(resultSet.getDate("return_date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate
        boolean checkedOut = resultSet.getBoolean("checked_out");

        return new Rental(id, bikeId, customerId, checkoutDate, dueDate, returnDate, checkedOut);
    }
}

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
public class Bicycle {
    public int id;
    public String make;
    public String model;
    public float costPerDay;
    public int bikeConditionId;

    private Bicycle(int id, String make, String model, float costPerDay, int bikeConditionId) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.costPerDay = costPerDay;
        this.bikeConditionId = bikeConditionId;
    }

    /**
     * Gets the BikeCondition object for this Bicycle represented by bikeConditionId
     *
     * @param connection - client's connection to the database
     * @return the BikeCondition object for this Bicycle
     * @throws SQLException if there was an error getting the BikeCondition
     */
    public BikeCondition getBikeCondition(Connection connection) throws SQLException {
        return BikeCondition.getById(connection, bikeConditionId);
    }

    /**
     * Inserts a new Bicycle into the database and returns a Bicycle model representing that entry
     *
     * @param connection - client's connection to the database
     * @param make - make of the Bicycle
     * @param model - model of the Bicycle
     * @param costPerDay - cost per day to rent the Bicycle
     * @param bikeConditionId - id representing the BikeCondition of this Bicycle
     * @return a Bicycle object representing the one inserted into the database
     * @throws SQLException if there was an error inserting the Bicycle object into the database
     */
    public static Bicycle createNewBicycle(Connection connection, String make, String model, float costPerDay, int bikeConditionId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Bicycle (make, model, cost_per_day, bike_condition_id) VALUES (?, ?, ?, ?)");
        preparedStatement.setString(1, make);
        preparedStatement.setString(2, model);
        preparedStatement.setFloat(3, costPerDay);
        preparedStatement.setInt(4, bikeConditionId);
        int result = preparedStatement.executeUpdate();

        if (result == 1) { // new Bicycle successfully inserted
            ResultSet bicycleRS =  connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS id");
            if (bicycleRS.next()) {
                return getById(connection, bicycleRS.getInt(1));
            } else {
                throw new SQLException("Unable to find new Bicycle in database");
            }
        } else { // Bicycle not inserted
            throw new SQLException("Unable to create new Bicycle");
        }
    }

    /**
     * Fetches a Bicycle represented by an id from the database
     *
     * @param connection - client's connection to the database
     * @param id - id representing a Bicycle in the database
     * @return the Bicycle represented by the provided id
     * @throws SQLException if there was an error finding the Bicycle in question
     */
    public static Bicycle getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM Bicycle b WHERE b.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return createFromResultSetRow(resultSet);
            } else {
                throw new SQLException("Bicycle with id: " + id + " not found");
            }
        } finally {
            ConnectionManager.closePreparedStatement(preparedStatement);
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    /**
     * Converts a ResultSet containing Bicycle entries into a List of Bicycle objects. Each row in the ResultSet must
     * contain all fields of Bicycle.
     *
     * @param resultSet - ResultSet containing rows that each have all Bicycle attributes
     * @return a List of Bicycle objects from the ResultSet
     * @throws SQLException if there was an error converting a row in the ResultSet to a Bicycle object
     */
    public static List<Bicycle> createListFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            List<Bicycle> bicycles = new ArrayList<>();

            while (resultSet.next()) {
                bicycles.add(createFromResultSetRow(resultSet));
            }

            return bicycles;
        } finally {
            ConnectionManager.closeResultSet(resultSet);
        }
    }

    /**
     * Converts the current row in a ResultSet to a Bicycle object. This method will not advance the row being examined
     * in the ResultSet - this is the responsibility of external code.
     *
     * @param resultSet - a ResultSet containing Bicycle objects
     * @return a Bicycle object from the current row in the ResultSet
     * @throws SQLException if there was an error converting the current row in the ResultSet to a Bicycle
     */
    private static Bicycle createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String make = resultSet.getString("make");
        String model = resultSet.getString("model");
        float costPerDay = resultSet.getFloat("cost_per_day");
        int bikeConditionId = resultSet.getInt("bike_condition_id");

        return new Bicycle(id, make, model, costPerDay, bikeConditionId);
    }

    public static void printBikeDetails(Connection connection, List<Bicycle> bicycles) throws SQLException {
        System.out.println("ID\tMake\t\tModel\t\tCost/Day\t\tCondition");
        for (Bicycle bike : bicycles) {
            System.out.println(String.format("%s\t%s\t\t%s\t\t%s\t\t%s", bike.id, bike.make, bike.model, bike.costPerDay, bike.getBikeCondition(connection).name));
        }
    }
}

package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/17/17.
 */
public class Bicycle {
    private int id;
    private String make;
    private String model;
    private float costPerDay;
    private int bikeConditionId;

    public Bicycle(int id, String make, String model, float costPerDay, int bikeConditionId) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.costPerDay = costPerDay;
        this.bikeConditionId = bikeConditionId;
    }

    public static Bicycle getById(int id) throws SQLException {
        
    }

    public BikeCondition getBikeCondition() throws SQLException {

    }

    public static List<Bicycle> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();

        while (resultSet.next()) {
            bicycles.add(createFromResultSetRow(resultSet));
        }

        return bicycles;
    }

    public static Bicycle createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String make = resultSet.getString("make");
        String model = resultSet.getString("model");
        float costPerDay = resultSet.getFloat("cost_per_day");
        int bikeConditionId = resultSet.getInt("bike_condition_id");

        return new Bicycle(id, make, model, costPerDay, bikeConditionId);
    }
}

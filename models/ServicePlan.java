package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/17/17.
 */
public class ServicePlan {
    public int bikeConditionId;
    public int offeredServiceId;

    private ServicePlan(int bikeConditionId, int offeredServiceId) {
        this.bikeConditionId = bikeConditionId;
        this.offeredServiceId = offeredServiceId;
    }

    public static void createNewServicePlan(Connection connection, int bikeConditionId, int offeredServiceId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ServicePlan (bike_condition_id, offered_service_id) VALUES (?, ?)");
        preparedStatement.setInt(1, bikeConditionId);
        preparedStatement.setInt(2, offeredServiceId);
        int result = preparedStatement.executeUpdate();

        if (result != 1) { // ServicePlan not inserted
            throw new SQLException("Unable to create new ServicePlan");
        }
    }

    /**
     * For a given bike condition (via its id), finds the list of services offered by the bike rental shop that are
     * part of the service plan to improve the bike's condition.
     *
     * @param connection - client's connection to the database
     * @param bikeConditionId - id representing a bike condition
     * @return a list of OfferedServices that are on the service plan for a particular bike condition
     * @throws SQLException if there was an error working with the database
     */
    public static List<OfferedService> getServicePlan(Connection connection, int bikeConditionId) throws SQLException {
        // Get the service plan links for a given bike condition
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ServicePlan sp WHERE sp.bike_condition_id = ?");
        preparedStatement.setInt(1, bikeConditionId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ServicePlan> servicePlanLinks = createListFromResultSet(resultSet);
        List<OfferedService> servicePlan = new ArrayList<>();

        // Populate a list of OfferedServices using the service plan connections previously fetched
        for (ServicePlan servicePlanLink : servicePlanLinks) {
            servicePlan.add(OfferedService.getById(connection, servicePlanLink.offeredServiceId));
        }

        return servicePlan;
    }

    public static List<ServicePlan> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<ServicePlan> servicePlans = new ArrayList<>();

        while (resultSet.next()) {
            servicePlans.add(createFromResultSetRow(resultSet));
        }

        return servicePlans;
    }

    public static ServicePlan createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int bikeConditionId = resultSet.getInt("bike_condition_id");
        int offeredServiceId = resultSet.getInt("offered_service_id");

        return new ServicePlan(bikeConditionId, offeredServiceId);
    }
}

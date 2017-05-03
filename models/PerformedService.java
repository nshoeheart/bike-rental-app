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
public class PerformedService {
    public int id;
    public int offeredServiceId;
    public int bikeId;
    public LocalDate datePerformed;

    private PerformedService(int id, int offeredServiceId, int bikeId, LocalDate datePerformed) {
        this.id = id;
        this.offeredServiceId = offeredServiceId;
        this.bikeId = bikeId;
        this.datePerformed = datePerformed;
    }

    public OfferedService getOfferedService(Connection connection) throws SQLException {
        return OfferedService.getById(connection, offeredServiceId);
    }

    public Bicycle getBicycle(Connection connection) throws SQLException {
        return Bicycle.getById(connection, bikeId);
    }

    public static PerformedService getById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PerformedService ps WHERE ps.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return createFromResultSetRow(resultSet);
        } else {
            throw new SQLException("PerformedService with id: " + id + " not found");
        }
    }

    public static List<PerformedService> createListFromResultSet(ResultSet resultSet) throws SQLException {
        List<PerformedService> performedServices = new ArrayList<>();

        while (resultSet.next()) {
            performedServices.add(createFromResultSetRow(resultSet));
        }

        return performedServices;
    }

    public static PerformedService createFromResultSetRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int offeredServiceId = resultSet.getInt("offered_service_id");
        int bikeId = resultSet.getInt("bike_id");
        LocalDate datePerformed = Instant.ofEpochMilli(resultSet.getDate("date_performed").getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); // convert from Date to LocalDate

        return new PerformedService(id, offeredServiceId, bikeId, datePerformed);
    }
}

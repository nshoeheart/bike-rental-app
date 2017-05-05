package clients;

import models.*;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalManagerClient {

    private static final int EXIT = 0;
    private static final int VIEW_CURRENT_RENTS = 1;    // Query A
    private static final int PROCESS_CHECKOUT = 2;      // Transaction A
    private static final int PROCESS_RETURN = 3;        // Transaction B and Query C
    private static final int VIEW_CONDITIONS = 4;       // Query E
    private static final int RECORD_SERVICE = 5;        // Transaction E

    public BikeRentalManagerClient(){}

    public void run() {
        Scanner scanner = null;

        try {
            scanner = new Scanner(System.in);
            int command = 1;

            while (command != 0) {

                // Menu for commands, manager must enter 0 to exit app
                System.out.print("Manager Command Menu:\n" +
                        "  0. Logout\n" +
                        "  1. View All Current Rentals\n" +
                        "  2. Process Rental Checkout\n" +
                        "  3. Process Rental Return\n" +
                        "  4. View Bike Conditions and Required Services\n" +
                        "  5. Record Service\n");

                // Get command from user
                System.out.print("Command number: ");
                command = Integer.parseInt(scanner.nextLine());

                if (command == EXIT) {
                    // Logs out and ends application
                    System.out.println("Logging out...");

                } else if (command == VIEW_CURRENT_RENTS) {

                    viewRentedBikes();

                } else if (command == PROCESS_CHECKOUT) {

                    processRentalCheckout(scanner);

                } else if (command == PROCESS_RETURN) {

                    processRentalReturn(scanner);

                } else if (command == VIEW_CONDITIONS) {

                    getBikeConditionsAndRequiredServices();

                } else if (command == RECORD_SERVICE) {

                    recordBikeService(scanner);
                } else {
                    System.out.println("Not a valid menu command");
                }

                System.out.println();
                System.out.println();
            }
        } finally {
            if (scanner != null) scanner.close();
        }
    }

    // Allows manager to see all bicycles that are currently rented out
    private void viewRentedBikes() {
        Connection connection = null;
        PreparedStatement getRentedBikes = null;

        try {
            connection = ConnectionManager.getConnection();

            getRentedBikes = connection.prepareStatement("SELECT * FROM Bicycle b WHERE EXISTS (SELECT * FROM Rental r WHERE r.bike_id = b.id AND r.checked_out = TRUE AND r.return_date IS NULL)");
            List<Bicycle> bicycles = Bicycle.createListFromResultSet(getRentedBikes.executeQuery());

            if (bicycles.isEmpty()) {
                System.out.println("No bicycles are currently rented out.");
            } else {
                // Print list of currently rented out bicycles
                System.out.println("Bicycles currently rented out:");
                Bicycle.printBikeDetails(connection, bicycles);
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closePreparedStatement(getRentedBikes);
            ConnectionManager.closeConnection(connection);
        }
    }


    // Allows a manager to checkout a bike to a customer given that today is the day of reservation
    private void processRentalCheckout(Scanner scanner) {
        Connection connection = null;
        PreparedStatement getRentalsToCheckout = null;
        PreparedStatement checkoutRental = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get a list of rentals that can be checked out (not yet checked out and today is the first day of reservation)
            getRentalsToCheckout = connection.prepareStatement("SELECT * FROM Rental r WHERE r.checked_out = FALSE AND r.checkout_date = ?");
            getRentalsToCheckout.setDate(1, Date.valueOf(LocalDate.now()));
            List<Rental> rentalsToCheckout = Rental.createListFromResultSet(getRentalsToCheckout.executeQuery());

            if (rentalsToCheckout.isEmpty()) {
                System.out.println("There are no bikes to be checked out today.");
            } else {
                // Print list of rentals that can be processed to be checked out today
                System.out.println("Rentals that can be processed for checkout today:");
                Rental.printSimpleRentalDetails(rentalsToCheckout);
                System.out.println();

                // Select which rental checkout to process
                System.out.print("ID of rental to checkout (or 0 to abort): ");
                int rentalId = Integer.parseInt(scanner.nextLine());

                // Actually process the checkout
                if (rentalId != 0) {
                    checkoutRental = connection.prepareStatement("UPDATE Rental r SET r.checked_out = TRUE WHERE r.id = ?");
                    checkoutRental.setInt(1, rentalId);
                    int success = checkoutRental.executeUpdate();

                    // Make sure the checkout was successful
                    if (success == 1) {
                        // Checkout was successful
                        System.out.println("Rental with id: " + rentalId + " checked out successfully");
                    } else {
                        // Checkout unsuccessful
                        throw new SQLException("Unable to process checkout for rental with ID: " + rentalId);
                    }
                }
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closePreparedStatement(getRentalsToCheckout);
            ConnectionManager.closePreparedStatement(checkoutRental);
            ConnectionManager.closeConnection(connection);
        }
    }


    // Allows manager to process a rental return
    private void processRentalReturn(Scanner scanner) {
        Connection connection = null;
        PreparedStatement getCheckedOutRentals = null;
        PreparedStatement processRentalReturn = null;
        PreparedStatement getReturnedRental = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get a list of rentals that are currently checked out
            getCheckedOutRentals = connection.prepareStatement("SELECT * FROM Rental r WHERE r.checked_out = TRUE AND r.return_date IS NULL");
            List<Rental> checkedOutRentals = Rental.createListFromResultSet(getCheckedOutRentals.executeQuery());

            if (checkedOutRentals.isEmpty()) {
                System.out.println("There are no rentals currently checked out");
            } else {
                // Print list of rentals that can be returned
                System.out.println("Rentals that can be processed for returns:");
                Rental.printSimpleRentalDetails(checkedOutRentals);
                System.out.println();

                System.out.print("ID of rental to return (or 0 to abort): ");
                int rentalId = Integer.parseInt(scanner.nextLine());

                if (rentalId != 0) {
                    processRentalReturn = connection.prepareStatement("UPDATE Rental r SET r.return_date = ? WHERE r.id = ?");
                    processRentalReturn.setDate(1, Date.valueOf(LocalDate.now()));
                    processRentalReturn.setInt(2, rentalId);
                    int success = processRentalReturn.executeUpdate();

                    // Make sure the return was successful
                    if (success == 1) {
                        // Return was successful, but don't show message yet just in case error later

                        // Get the rental that was just returned
                        Rental returnedRental = null;
                        for (Rental rental : checkedOutRentals) {
                            if (rentalId == rental.id) returnedRental = rental;
                        }

                        if (returnedRental == null) {
                            throw new SQLException("Error getting data for returned rental");
                        } else {
                            System.out.println("Rental with ID: " + rentalId + " returned successfully");

                            // Get necessary data for cost calculations
                            LocalDate checkoutDate = returnedRental.checkoutDate;
                            LocalDate returnDate = LocalDate.now();
                            LocalDate dueDate = returnedRental.dueDate;
                            float costPerDay = returnedRental.getBicycle(connection).costPerDay;

                            System.out.println("Checkout date: " + checkoutDate.toString());
                            System.out.println("Due date: " + dueDate.toString());
                            System.out.println("Return date: " + returnDate.toString());

                            // Perform cost calculations (rate doubled for days past end of rental reservation)
                            float baseCost = costPerDay * (float) (ChronoUnit.DAYS.between(checkoutDate, dueDate));
                            long returnDayDiff = (ChronoUnit.DAYS.between(dueDate, returnDate)) + 1;

                            // Print rental cost summary
                            if (returnDayDiff == 0) {
                                // Return is on time
                                System.out.printf("Total cost of rental: $%.2f\n", baseCost);
                            } else if (returnDayDiff > 0) {
                                // Return is late, add late fee rate to cost
                                float lateFees = 2f * costPerDay * ((float) returnDayDiff);
                                System.out.printf("Base cost of rental: $%.2f\n", baseCost);
                                System.out.printf("Late fees: $%.2f\n", lateFees);
                                System.out.printf("Total cost of rental: $%.2f\n", baseCost + lateFees);
                            } else {
                                // Return is early, apply refund to base cost
                                float refund = -1f * costPerDay * ((float) returnDayDiff);
                                System.out.printf("Base cost of rental: $%.2f\n", baseCost);
                                System.out.printf("Refund: $%.2f\n", refund);
                                System.out.printf("Total cost of rental: $%.2f\n", baseCost - refund);
                            }
                        }
                    } else {
                        throw new SQLException("Unable to process return for rental with ID: " + rentalId);
                    }
                }
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getCheckedOutRentals);
            ConnectionManager.closePreparedStatement(processRentalReturn);
            ConnectionManager.closePreparedStatement(getReturnedRental);
        }
    }

    // Get a list of all bikes, their conditions, and the services required
    private void getBikeConditionsAndRequiredServices() {
        Connection connection = null;
        PreparedStatement getBicycles = null;

        try {
            connection = ConnectionManager.getConnection();

            getBicycles = connection.prepareStatement("SELECT * FROM Bicycle b");
            List<Bicycle> bicycles = Bicycle.createListFromResultSet(getBicycles.executeQuery());

            if (bicycles.isEmpty()) {
                System.out.println("There are no bicycles in the shop");
            } else {
                // For each bicycle, print information (including its condition) and its service plan
                System.out.println("Bicycles in shop and their required services:");
                Bicycle.printBikeDetailsAndServicePlans(connection, bicycles);
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getBicycles);
        }
    }

    // Performs selected service on selected bike on today's date
    private void recordBikeService(Scanner scanner) {
        Connection connection = null;
        PreparedStatement getBicycles = null;
        PreparedStatement getOfferedServices = null;
        PreparedStatement recordService = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get a list of all bicycles in the shop
            getBicycles = connection.prepareStatement("SELECT * FROM Bicycle b");
            List<Bicycle> bicycles = Bicycle.createListFromResultSet(getBicycles.executeQuery());

            // Prompt manager to pick a bike to perform service on today
            if (bicycles.isEmpty()) {
                System.out.println("There are no bicycles in the shop");
            } else {
                // Print list of bikes
                System.out.println("Bikes in shop that can be serviced:");
                Bicycle.printBikeDetails(connection, bicycles);
                System.out.println();

                // Prompt manager to select ID of bike to service
                System.out.print("ID of bike to perform service on today (or 0 to abort): ");
                int bikeId = Integer.parseInt(scanner.nextLine());

                if (bikeId != 0) {
                    // Get list of available services to perform
                    getOfferedServices = connection.prepareStatement("SELECT * FROM OfferedService os");
                    List<OfferedService> offeredServices = OfferedService.createListFromResultSet(getOfferedServices.executeQuery());

                    if (offeredServices.isEmpty()) {
                        System.out.println("There are no services that can be performed on bikes");
                    } else {
                        // Print list of offered services
                        System.out.println("Services that can be performed:");
                        OfferedService.printOfferedServiceDetails(offeredServices);
                        System.out.println();

                        // Prompt manager to select ID of service to perform
                        System.out.print("ID of service to perform today (or 0 to abort): ");
                        int offeredServiceId = Integer.parseInt(scanner.nextLine());

                        if (offeredServiceId != 0) {
                            // Record that this service was performed on this bike today
                            recordService = connection.prepareStatement("INSERT INTO PerformedService (offered_service_id, bike_id, date_performed) VALUES (?, ?, ?)");
                            recordService.setInt(1, offeredServiceId);
                            recordService.setInt(2, bikeId);
                            recordService.setDate(3, Date.valueOf(LocalDate.now()));
                            int success = recordService.executeUpdate();

                            // Make sure the insert was successful
                            if (success == 1) {
                                // Insert was successful
                                System.out.println("Service with ID: " + offeredServiceId + " successfully recorded as performed on bike with ID: " + bikeId);
                            } else {
                                // Insert was not successful
                                throw new SQLException("Unable to record service with ID: " + offeredServiceId + " performed on bike with ID: " + bikeId);
                            }
                        }
                    }
                }
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closePreparedStatement(getBicycles);
            ConnectionManager.closePreparedStatement(getOfferedServices);
            ConnectionManager.closePreparedStatement(recordService);
            ConnectionManager.closeConnection(connection);
        }
    }
}

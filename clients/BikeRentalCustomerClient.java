package clients;

import models.Bicycle;
import models.Customer;
import models.Rental;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalCustomerClient {

    private Customer customer;

    private static final int EXIT = 0;
    private static final int VIEW_CURRENT_AVAIL_BIKES = 1;  // Query B
    private static final int MAKE_RESERVATION = 2;          // Transaction C
    private static final int LIST_FUTURE_RESERVATIONS = 3;  // Query D and Transaction D

    public BikeRentalCustomerClient(Customer customer) {
        this.customer = customer;

        System.out.println("Logged in to bike rental store customer client");
        System.out.println("\tCustomer ID: " + customer.id);
        System.out.println("\t  Last Name: " + customer.lastName);
        System.out.println("\t First Name: " + customer.firstName);
        System.out.println();
    }

    public void run() {
        Scanner scanner = null;

        try {
            scanner = new Scanner(System.in);
            int command = 1;

            while (command != EXIT) {
                // Menu for commands, customer must enter 0 to exit app
                System.out.print("Customer command menu:\n" +
                        "  0. Logout\n" +
                        "  1. View Currently Available Bikes\n" +
                        "  2. Make a Rental Reservation\n" +
                        "  3. View (And Optionally Cancel) Your Future Reservations\n");

                // Get command from user
                System.out.print("Command number: ");
                command = Integer.parseInt(scanner.nextLine());

                if (command == EXIT) {
                    // Logs out and ends application
                    System.out.println("Logging out...");

                } else if (command == VIEW_CURRENT_AVAIL_BIKES) {

                    viewCurrentlyAvailableBikes();

                } else if (command == MAKE_RESERVATION) {

                    makeRentalReservation(customer.id, scanner);

                } else if (command == LIST_FUTURE_RESERVATIONS) {

                    listFutureReservations(customer.id, scanner);

                } else {
                    System.out.println("Not a valid menu command");
                }

                System.out.println();
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
        }
    }

    // Shows bikes that are currently available for rent
    private static void viewCurrentlyAvailableBikes() {
        Connection connection = null;
        PreparedStatement getCurrentlyAvailableBikes = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get bikes that are both not currently checked out and are not reserved for checkout today
            getCurrentlyAvailableBikes = connection.prepareStatement("SELECT * FROM Bicycle b WHERE NOT EXISTS " +
                    "(SELECT * FROM Rental r WHERE r.bike_id = b.id AND " +
                    "((r.checked_out = TRUE AND r.return_date IS NULL) OR (r.checkout_date = ?)))");
            getCurrentlyAvailableBikes.setDate(1, Date.valueOf(LocalDate.now()));
            List<Bicycle> currentlyAvailableBikes = Bicycle.createListFromResultSet(getCurrentlyAvailableBikes.executeQuery());

            if (currentlyAvailableBikes.isEmpty()) {
                System.out.println("There are no bicycles currently available for rent");
            } else {
                System.out.println("Bicycles currently available for rent:");
                Bicycle.printBikeDetails(connection, currentlyAvailableBikes);
            }

            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getCurrentlyAvailableBikes);
        }
    }

    // Allows user to make a reservation for a bike rental
    private static void makeRentalReservation(int customerId, Scanner scanner) {
        Connection connection = null;
        PreparedStatement getAvailableBikes = null;
        PreparedStatement reserveBikeRental = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get rental checkout date
            System.out.print("Enter the date you wish to rent the bike (format like YYYY-MM-DD, including the hyphens): ");
            String inputDate = scanner.nextLine();
            LocalDate checkoutDate = LocalDate.parse(inputDate);

            // Get rental due date
            System.out.print("Enter number of days to rent: ");
            int length = Integer.parseInt(scanner.nextLine());
            LocalDate dueDate = checkoutDate.plusDays(length);

            // Find bikes that are available for the entirety of the desired date range
            getAvailableBikes = connection.prepareStatement("SELECT * FROM Bicycle b WHERE " +
                    "NOT EXISTS (SELECT * FROM Rental r WHERE r.bike_id = b.id " +
                    "AND ((? BETWEEN r.checkout_date AND r.due_date) OR (? BETWEEN r.checkout_date AND r.due_date)))");
            getAvailableBikes.setDate(1, Date.valueOf(checkoutDate));
            getAvailableBikes.setDate(2, Date.valueOf(dueDate));
            List<Bicycle> availableBikes = Bicycle.createListFromResultSet(getAvailableBikes.executeQuery());

            if (availableBikes.isEmpty()) {
                System.out.println("There are no bikes available for rent over the date range " + checkoutDate + " to " + dueDate);
            } else {
                // Print list of available bikes
                System.out.println("Available bicycles for rent from " + checkoutDate + " to " + dueDate);
                Bicycle.printBikeDetails(connection, availableBikes);
                System.out.println();

                // Prompt user to select bike for rental reservation
                System.out.print("ID of bike to rent from " + checkoutDate + " to " + dueDate + " (or 0 to abort): ");
                int bikeId = Integer.parseInt(scanner.nextLine());

                if (bikeId != 0) {
                    // Attempt to reserve the bike rental
                    reserveBikeRental = connection.prepareStatement("INSERT INTO Rental (bike_id, customer_id, checkout_date, due_date, return_date, checked_out) VALUES (?, ?, ?, ?, NULL, FALSE)");
                    reserveBikeRental.setInt(1, bikeId);
                    reserveBikeRental.setInt(2, customerId);
                    reserveBikeRental.setDate(3, Date.valueOf(checkoutDate));
                    reserveBikeRental.setDate(4, Date.valueOf(dueDate));
                    int success = reserveBikeRental.executeUpdate();

                    // Check to see if the rental was reserved successfully
                    if (success == 1) {
                        // Rental successfully reserved
                        System.out.println("Rental for bike with ID: " + bikeId + " successfully reserved from " + checkoutDate + " to " + dueDate);
                    } else {
                        // Rental reservation failed
                        throw new SQLException("Failed to reserve rental for bike with ID: " + bikeId + " from " + checkoutDate + " to " + dueDate);
                    }
                }
            }

            // If successful to this point, commit transaction
            connection.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getAvailableBikes);
            ConnectionManager.closePreparedStatement(reserveBikeRental);
        }
    }

    // Shows a user their future rental reservations and allow user to cancel one if they choose
    private static void listFutureReservations(int customerId, Scanner scanner) {
        Connection connection = null;
        PreparedStatement getFutureReservations = null;
        PreparedStatement cancelFutureReservation = null;

        try {
            connection = ConnectionManager.getConnection();

            // Get a list of future rental reservations for this customer
            getFutureReservations = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.checkout_date > ?");
            getFutureReservations.setInt(1, customerId);
            getFutureReservations.setDate(2, Date.valueOf(LocalDate.now()));
            List<Rental> futureRentals = Rental.createListFromResultSet(getFutureReservations.executeQuery());

            if (futureRentals.isEmpty()) {
                System.out.println("You do not currently have any future reservations");
            } else {
                // Print a list of future rental reservations for this customer
                System.out.println("Future bike rental reservations:");
                Rental.printSimpleRentalDetails(futureRentals);
                System.out.println();

                // Allow customer to cancel a reservation if they choose
                System.out.print("Enter ID of rental reservation to cancel it (or 0 to exit): ");
                int rentalId = Integer.parseInt(scanner.nextLine());

                if (rentalId != 0) {
                    // Cancel the selected reservation
                    cancelFutureReservation = connection.prepareStatement("DELETE FROM Rental WHERE id = ?");
                    cancelFutureReservation.setInt(1, rentalId);
                    int success = cancelFutureReservation.executeUpdate();

                    // Make sure the reservation was actually cancelled
                    if (success == 1) {
                        // Reservation cancelled successfully
                        System.out.println("Rental reservation with id: " + rentalId + " cancelled successfully");
                    } else {
                        // Reservation not cancelled
                        throw new SQLException("Unable to cancel rental reservation with id: " + rentalId);
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getFutureReservations);
            ConnectionManager.closePreparedStatement(cancelFutureReservation);
        }
    }
}

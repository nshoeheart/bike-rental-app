package clients;

import models.Bicycle;
import models.Customer;
import models.Rental;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
                System.out.print("Please enter the number of the command you wish to do:\n" +
                        "  0. Logout\n" +
                        "  1. View Currently Available Bikes\n" +
                        "  2. View Your Future Reservations\n" +
                        "  3. Make a Rental Reservation\n" +
                        "  4. Cancel a Future Reservation\n ");

                // Get command from user
                command = Integer.parseInt(scanner.nextLine());

                if (command == EXIT) {
                    // Logs out and ends application
                    System.out.println("Logging out...");

                } else if (command == VIEW_CURRENT_AVAIL_BIKES) {

                    viewCurrentlyAvailableBikes(customer.id);

                } else if (command == MAKE_RESERVATION) {

                    makeRentalReservation(customer.id);

                } else if (command == LIST_FUTURE_RESERVATIONS) {

                    menuOption3(customer.id);

                } else {
                    System.out.println("Not a valid menu command");
                }
            }
        } finally {
            if (scanner != null) scanner.close();
        }
    }

    // Shows bikes that are currently available for rent
    private static void viewCurrentlyAvailableBikes(int customerId) {
        Connection connection = null;
        PreparedStatement getCurrentlyAvailableBikes = null;

        try {
            connection = ConnectionManager.getConnection();

            //todo insert code here
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getCurrentlyAvailableBikes);
        }
    }

    // Allows user to make a reservation for a bike rental
    private static void makeRentalReservation(int customerId) {
        Connection connection = null;
        PreparedStatement getAvailableBikes = null;
        Scanner scanner = null;

        try {
            connection = ConnectionManager.getConnection();
            scanner = new Scanner(System.in);

            // Get rental checkout date
            System.out.print("Enter the date you wish to rent the bike (format like YYYY-MM-DD, including the hyphens): ");
            String in = scanner.nextLine();
            LocalDate checkoutDate = LocalDate.parse(in);

            // Boolean to check if customer will rent bike today, (this will be the checked_out field in Rental)
            boolean checkoutToday = checkoutDate.equals(LocalDate.now());

            // Get rental due date
            System.out.println("How many days would you like to rent it?");
            int length = Integer.parseInt(scanner.nextLine());
            LocalDate dueDate = checkoutDate.plusDays(length);

            // Allows user to query to DB based on commands they select
            //getAvailableBikes = connection.prepareStatement("SELECT * FROM Rental r WHERE r.checked_out = false AND NOT r.return_date IS NULL");
            getAvailableBikes = connection.prepareStatement("SELECT * FROM Rental r WHERE r.");
            List<Bicycle> availableBikes = Bicycle.createListFromResultSet(getAvailableBikes.executeQuery());

            System.out.println("Please select one of the following bikes to rent by entering the id associated with the bike; "
                    + "\n type '0' to go back to main menu:");

            // Prints each result from Rental table that are available for customer to rent
            System.out.println("ID\tMake\t\tModel\t\tCost/Day\t\tCondition");
            for (Bicycle bike : availableBikes) {
                System.out.println(String.format("%s\t%s\t\t%s\t\t%s\t\t%s", bike.id, bike.make, bike.model, bike.costPerDay, bike.getBikeCondition(connection).name));
            }

            System.out.print("Bike ID: ");
            int bikeSelection = Integer.parseInt(scanner.nextLine());

            if (bikeSelection != 0) {
                try {
                    // If bike is rented, attempt to update it in database to have rented status
                    PreparedStatement getBike = connection.prepareStatement("SELECT * FROM Rental r WHERE r.id = ?");
                    getBike.setInt(1, bikeSelection);
                    Rental referenceRental = Rental.createFromResultSetRow(getBike.executeQuery(), true);
                    // Sets parameters of insertion accordingly, then executes
                    Rental.createNewRental(connection, referenceRental.bikeId, customerId, checkoutDate, dueDate, null, checkoutToday);
                } catch (Exception e) {
                    System.out.print("Failed to rent the requested bike.");

                }
                // If successful, commit transaction (otherwise should not reach this point)
                connection.commit();
            } else {
                System.out.println("Exiting Rental Process...");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getAvailableBikes);
            if (scanner != null) scanner.close();
        }
    }

    // Shows a user their future rental reservations and allow user to cancel one if they choose
    private static void listFutureReservations(int customerId) {
        Scanner scanner = null;
        Connection connection = null;
        PreparedStatement getCurrentlyAvailableBikes = null;

        try {
            connection = ConnectionManager.getConnection();
            scanner = new Scanner(System.in);

            //todo insert code here
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
            ConnectionManager.closePreparedStatement(getCurrentlyAvailableBikes);
            if (scanner != null) scanner.close();
        }
    }


    // Allows user to view their own outstanding rentals
    private static void menuOption2(int customerId) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();

            //todo insert code for this menu option


            // Retrieves customer rentals
            System.out.println("Getting rentals");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.return_date IS NULL");
            preparedStatement.setInt(1, customerId);
            List<Rental> customerRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());

            if (customerRentals.isEmpty()) {
                System.out.println("Rentals list empty");
            } else {
                // Prints out customer's rentals
                System.out.println("ID\tMake\t\tModel\t\tCost/Day\t\tCondition");
                for (Rental rental : customerRentals) {
                    Bicycle bike = rental.getBicycle(connection);
                    System.out.println(String.format("%s\t%s\t\t%s\t\t%s\t\t%s", bike.id, bike.make, bike.model, bike.costPerDay, bike.getBikeCondition(connection).name));
                }
            }

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }


    // Allows user to cancel one of their reservations if it is before the checkout date
    private static void menuOption3(int customerId) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            Scanner scanner = new Scanner(System.in);

            //todo insert code for this menu option

            // Retrieves customer rentals beyond current date
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.checkout_date <= ? AND r.checked_out = true");
            preparedStatement.setInt(1, customerId);
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            List<Rental> customerRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());


            System.out.println("Which of these rentals would you like to cancel? Please enter the id number associated with the rental. ");

            // Prints out those customer's rentals
            for (int i = 0; i < customerRentals.size(); i++) {
                System.out.println(customerRentals.get(i).toString());
            }

            // Gets user input
            int cancel = scanner.nextInt();


            // Deletes the rental from the table completely
            PreparedStatement cancelRental = connection.prepareStatement("DELETE FROM Rental where id = ?");
            cancelRental.setInt(1, cancel);
            cancelRental.executeQuery();

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    // Show a user's future
    private static void menuOption4(int customerId) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            Scanner scanner = new Scanner(System.in);

            //todo insert code for this menu option

            // Queries up outstanding rentals for user and puts them in a list
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r where r.customer_id = ? AND r.checked_out = true AND r.return_date IS NULL");
            preparedStatement.setInt(1, customerId);
            List<Rental> outstandingRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());

            System.out.println("Please select from the following outstanding rentals to return by typing the id number associated with the rental you wish to return.");
            // Prints outstanding rentals
            for (int i = 0; i < outstandingRentals.size(); i++) {
                System.out.println(outstandingRentals.get(i).toString());
            }

            int ret = scanner.nextInt();

            PreparedStatement Return = connection.prepareStatement("UPDATE Rental r SET r.return_date = ? AND r.checked_out = false WHERE r.id = ?");
            Return.setDate(1, Date.valueOf(LocalDate.now()));
            Return.setInt(2, ret);
            Rental returnedRental = Rental.createFromResultSetRow(Return.executeQuery(), true);
            Return.executeQuery();

            // Attempt at processing a refund
            long daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), returnedRental.dueDate);
            float bikeCost = returnedRental.getBicycle(connection).costPerDay;
            float refund = daysDiff * bikeCost;

            System.out.println("Total refund is " + Float.toString(refund));

            // If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

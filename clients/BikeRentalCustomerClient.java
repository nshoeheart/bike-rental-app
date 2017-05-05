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
    private static final int GET_AVAIL_BIKES = 1; // Scenario covers query b and transaction c
    private static final int VIEW_CUST_RENTALS = 2; // Scenario covers query a and d
    private static final int CANCEL_RESERVATION = 3; // Scenario covers transaction d
    private static final int MAKE_RETURN = 4; // Scenario covers transaction b

    public BikeRentalCustomerClient(Customer customer) {
        this.customer = customer;

        System.out.println("Logged in to bike rental store customer client");
        System.out.println("\tCustomer ID: " + customer.id);
        System.out.println("\t  Last Name: " + customer.lastName);
        System.out.println("\t First Name: " + customer.firstName);
        System.out.println();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        // User input command, checked each loop
        int command = 1;

        while (command != EXIT) {

            //Menu for commands, customer must enter 0 to exit app
            System.out.print("Please enter the number of the command you wish to do:\n  0. Logout\n  1. Rent Bike\n  "
                    + "2. View Your Rentals\n  3. Cancel a Reservation\n  4. Make a Return\n ");

            //Check user input
            command = Integer.parseInt(scanner.nextLine());

                
                /*
                 * Exit case
                 */

            if (command == EXIT) {
                // Logs out and ends application
                System.out.println("Logging out...");
                    
                /* 
                 * View bikes available to Rent  
                 * Covers query b and transaction c 
                 *   
                 */

            } else if (command == GET_AVAIL_BIKES) {
                menuOption1(customer.id);
                    
                /*
                 *  View user's rentals
                 *  Covers query a and d
                 *  
                 */

            } else if (command == VIEW_CUST_RENTALS) {

                menuOption2(customer.id);
                    
                /*
                 *  Cancel a reservation before the check out date 
                 *  Covers transaction d 
                 */

            } else if (command == CANCEL_RESERVATION) {

                menuOption3(customer.id);
                
                /*
                 *  Make a return on a bike user currently has outstanding	
                 */
            } else if (command == MAKE_RETURN) {

                menuOption4(customer.id);

            } else {
                System.out.println("Not a valid menu command. Please re-enter command option: ");
                command = Integer.parseInt(scanner.nextLine());
            }
        }
    }


    // Allows user to see bikes available for rent
    private static void menuOption1(int customer_id) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();

            //todo insert code for this menu option

            Scanner scanner = new Scanner(System.in);

            //Links to Rental table and returns all bikes that can be rented out
            int rent = 1;

            // Allows user to query to DB based on commands they select
            Statement statement1 = connection.createStatement();
            ResultSet result1 = statement1.executeQuery("SELECT * FROM Rental r WHERE r.checked_out = false AND r.return_date != NULL"); // Returns in result all bikes on rental table available for rental
            System.out.print("Please select one of the following bikes to rent by entering the id associated with the bike; "
                    + "\n type '0' to go back to main menu:\n");

            List<Bicycle> availableBikes = Bicycle.createListFromResultSet(result1);

            // Prints each result from Rental table that are available for customer to rent
            for (int i = 0; i < availableBikes.size(); i++) {
                System.out.println(availableBikes.get(i).toString()); // bike data
            }

            rent = Integer.parseInt(scanner.nextLine());

            if(rent != 0){
            	
            	// Gets more info regarding the rental
            	System.out.println("Enter the date you wish to rent the bike (format like YYYY-MM-DD, including the hyphens.)");
            	String in = scanner.nextLine();
            	LocalDate checkout = LocalDate.parse(in);


            	// Boolean to check if customer will rent bike today, (this will be the checked_out field in Rental)
            	Boolean bool = false;
            	if (checkout == LocalDate.now()) {
            		bool = true;
            	}

            	System.out.println("How many days would you like to rent it?");
            	int length = Integer.parseInt(scanner.nextLine());

            	// Process for establishing rental dates
            	LocalDate due = checkout.plusDays(length);

            	if (rent != 0) { //Case where bike is rented, then we need to update the rental table
            		try {
            			// If bike is rented, attempt to update it in database to have rented status
            			PreparedStatement getBike = connection.prepareStatement("SELECT * FROM Rental r WHERE r.id = ?");
            			getBike.setInt(1, rent);
            			Rental referenceRental = Rental.createFromResultSetRow(getBike.executeQuery());
            			// Sets parameters of insertion accordingly, then executes
            			Rental rentBike = Rental.createNewRental(connection, referenceRental.bikeId, customer_id, checkout, due, null, bool);

            		} catch (Exception e) {
            			System.out.print("Failed to rent the requested bike.");

            		}
            	}
            	// If successful, commit transaction (otherwise should not reach this point)
            	connection.commit();
            }else {
            	System.out.println("Exiting Rental Process...");
            }
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
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

            if (customerRentals.isEmpty()) System.out.println("Rentals list empty");
            // Prints out customer's rentals
            for (Rental rent : customerRentals){
                System.out.println(rent.bikeId);
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
    private static void menuOption3(int customer_id) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            Scanner scanner = new Scanner(System.in);

            //todo insert code for this menu option

            // Retrieves customer rentals beyond current date
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.checkout_date <= ? AND r.checked_out = true");
            preparedStatement.setInt(1, customer_id);
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

    // Allows user to return a bike they have outstanding and get a refund if returned before due date
    private static void menuOption4(int customer_id) {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            Scanner scanner = new Scanner(System.in);

            //todo insert code for this menu option

            // Queries up outstanding rentals for user and puts them in a list
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r where r.customer_id = ? AND r.checked_out = true AND r.return_date = NULL");
            preparedStatement.setInt(1, customer_id);
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
            Rental returnedRental = Rental.createFromResultSetRow(Return.executeQuery());
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

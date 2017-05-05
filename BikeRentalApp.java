import clients.BikeRentalCustomerClient;
import clients.BikeRentalManagerClient;
import models.Customer;
import util.Bootstrap;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nathan on 4/17/17.
 */
public class BikeRentalApp {
    public static void main(String[] args) {
        try {
            // Collect the command line arguments in a list
            List<String> cmdArgs = Arrays.asList(args);

            // If 'bootstrap' arg is present, run sql scripts to create tables and insert bootstrap data
            if (cmdArgs.contains("bootstrap")) {
                try {
                    System.out.println("Bootstrapping Database...");
                    Bootstrap.bootstrapDatabase();
                    System.out.println("Database bootstrapped successfully");
                } catch (SQLException e) {
                    System.out.println("Error bootstrapping database:");
                    e.printStackTrace();
                }
            }

            // If both 'manager' and 'customer' args are present, show error message and exit (can only choose one or the other)
            if (cmdArgs.contains("manager") && cmdArgs.contains("customer")) {
                System.out.println("Please select either manager or customer");
                System.exit(0);
            }

            // If 'manager' arg present, run manager client
            if (cmdArgs.contains("manager")) {
                System.out.println("Starting new manager client...");
                new BikeRentalManagerClient().run();
            }

            // If 'customer' arg present, run customer client
            if (cmdArgs.contains("customer")) {
                Customer customer = logCustomerIn();
                System.out.println("Starting new customer client...");
                new BikeRentalCustomerClient(customer).run();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the customer login process, whether by finding an existing customer or creating a new one
     *
     * @return a Customer with which to start a client
     * @throws SQLException if the login process failed
     */
    private static Customer logCustomerIn() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String customerIdInput = "";

        // Ask for customer ID input, repeat if invalid
        while (customerIdInput.isEmpty()) {
            System.out.print("Enter Customer ID (or 0 for new user): ");
            customerIdInput = scanner.nextLine().trim();
            if (!isValidCustomerIdInput(customerIdInput)) {
                System.out.println("Invalid input");
            }
        }

        System.out.println();
        int customerId = Integer.parseInt(customerIdInput);
        Connection connection = null;

        // Process instructions with valid customer ID input
        try {
            connection = ConnectionManager.getConnection();
            Customer customer;

            if (customerId == 0) {
                System.out.println("Starting new customer creation process...");
                customer = getNewCustomer(connection);
                System.out.println("New customer created successfully\n");
            } else {
                System.out.println("Fetching customer data...");
                customer = Customer.getById(connection, customerId);
                System.out.println("Customer data found successfully\n");
            }

            // If no errors, commit the transaction and return the Customer model
            connection.commit();
            return customer;
        } catch (SQLException e) {
            // Getting or creating customer failed, rollback transaction
            ConnectionManager.rollbackConnection(connection);
            throw new SQLException("Customer login failed - " + e.getMessage());
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    /**
     * Handles creation of a new Customer
     *
     * @param connection - connection to the database
     * @return a new Customer object to start a client for
     * @throws SQLException if there was an error creating a new Customer
     */
    private static Customer getNewCustomer(Connection connection) throws SQLException {
        String lastName = "";
        String firstName = "";
        Scanner scanner = new Scanner(System.in);

        // Get input for the customer's last name
        while (lastName.isEmpty()) {
            System.out.print("Last Name: ");
            lastName = scanner.nextLine();
        }

        // Get input for the customer's first name
        while (firstName.isEmpty()) {
            System.out.print("First Name: ");
            firstName = scanner.nextLine();
        }

        // Create the customer and insert into the database
        return Customer.createNewCustomer(connection, lastName, firstName);
    }

    /**
     * Checks to see if a string is a valid customer ID input; that is, it must both be an integer and be 0 or greater.
     *
     * @param input - String input to check
     * @return whether or not the input string was a valid customer ID input
     */
    private static boolean isValidCustomerIdInput(String input) {
        try {
            return (Integer.parseInt(input) >= -1);
        } catch (Exception e) {
            return false;
        }
    }
}

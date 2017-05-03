package clients;

import models.Bicycle;
import models.Customer;
import models.Rental;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private static final int MAKE_RETURN = 4; // Scenario covers query c
    
    private Date currentdate = (Date) Date.from((LocalDate.now()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    
    public BikeRentalCustomerClient(Customer customer) {
        this.customer = customer;
        //todo "login" either by getting the customer's existing info from database or prompting for user creation
    }

    public void run() {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = ConnectionManager.getConnection();

            // User input command, checked each loop
            int command = 1;

            while (command != EXIT) {

                //Menu for commands, customer must enter 0 to exit app
                System.out.print("Please enter the number of the command you wish to do:\n  0. Logout\n  1. Rent Bike\n  "
                        + "2. View Your Rentals\n  3. Request a Service\n");

                //Check user input
                command = Integer.parseInt(scanner.nextLine());

                
                /*
                 * Exit case
                 */
                
                if (command == 0) {
                    // Logs out and ends application
                    System.out.println("Logging out...");
                    
                /* 
                 * View bikes available to Rent  
                 * Covers query b and transaction c 
                 * READY FOR TESTING  
                 */
                    
                } else if (command == GET_AVAIL_BIKES) {

                    //Links to Rental table and returns all bikes that can be rented out
                    int rent = 1;

                    // Allows user to query to DB based on commands they select
                    Statement statement1 = connection.createStatement();
                    ResultSet result1 = statement1.executeQuery("SELECT * FROM Rental r WHERE r.checked_out = false AND return_date != NULL"); // Returns in result all bikes on rental table available for rental
                    System.out.print("Please select one of the following bikes to rent by entering the id associated with the bike; "
                            + "\n type '0' to go back to main menu:\n");

                    List<Bicycle> availableBikes = Bicycle.createListFromResultSet(result1);

                    // Prints each result from Rental table that are available for customer to rent
                    for (int i = 0; i < availableBikes.size(); i++){
                        System.out.println(availableBikes.get(i).toString()); // bike data
                    }

                    rent = Integer.parseInt(scanner.nextLine());
                    
                    // Gets more info regarding the rental
                    System.out.println("Enter the date you wish to rent the bike (format like MMM DD YYYY, including the spaces where MMM = Jan, Feb, etc.)");
                    String in = scanner.nextLine();
                    LocalDate date = LocalDate.parse(in); 
                    
                    Date checkout = (Date) Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    
                    
                    // Boolean to check if customer will rent bike today; note: will checkout bike to customer if rental is same day
                    Boolean bool = false;
                    if(date == LocalDate.now()){bool = true;}

                    System.out.println("How many days would you like to rent it?");
                    int length = Integer.parseInt(scanner.nextLine());
                    
                    // Process for establishing rental dates
                    LocalDate temp = date.plusDays(length);
                    Date due = (Date) Date.from(temp.atStartOfDay(ZoneId.systemDefault()).toInstant());

                    

                    if (rent != 0) { //Case where bike is rented, then we need to update the rental table
                        try {
                            // If bike is rented, attempt to update it in database to have rented status
                        	PreparedStatement getBike = connection.prepareStatement("SELECT * FROM Rental r WHERE r.id = ?");
                        	getBike.setInt(1, rent);
                        	Rental referenceRental = Rental.createFromResultSetRow(getBike.executeQuery());
                            PreparedStatement rentBike = connection.prepareStatement("INSERT INTO Rental (bike_id, customer_id, checkout_date, due_date, return_date, checked_out)"
                            		+ " VALUES (? ? ? ? ? ?)"); //queries and updates table appropriately
                            
                            // Sets parameters of insertion accordingly, then executes
                            rentBike.setInt(1, referenceRental.bikeId);
                            rentBike.setInt(2, customer.id);
                            rentBike.setDate(3, checkout);
                            rentBike.setDate(4, due);
                            rentBike.setDate(5, null);
                            rentBike.setBoolean(6, bool);
                            rentBike.executeUpdate();

                        } catch (Exception e) {
                            System.out.print("Failed to rent the requested bike.");

                        }
                    }

                    
                /*
                 *  View user's rentals
                 *  Covers query a and d
                 *  READY FOR TESTING
                 */
                    
                } else if (command == VIEW_CUST_RENTALS) {

                    // Retrieves customer rentals
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.checkout_date >= ?");
                    preparedStatement.setInt(1, customer.id);
                    preparedStatement.setDate(2, new Date(LocalDate.now().toEpochDay()));
                    List<Rental> customerRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());

                    // Prints out customer's rentals
                    for(int i = 0; i < customerRentals.size(); i++){
                    	System.out.println(customerRentals.get(i).toString());
                    }
                    
                /*
                 *  Cancel a reservation before the check out date 
                 *  Covers transaction d 
                 */
                    
                }else if (command == CANCEL_RESERVATION){
                	
                	// Retrieves customer rentals beyond current date
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r WHERE r.customer_id = ? AND r.checkout_date >= ?");
                    preparedStatement.setInt(1, customer.id);
                    preparedStatement.setDate(2, new Date(LocalDate.now().toEpochDay()));
                    List<Rental> customerRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());
                    
                    
                    System.out.println("Which of these rentals would you like to cancel? Please enter the id number associated with the rental");
                    
                    // Prints out those customer's rentals
                    for(int i = 0; i < customerRentals.size(); i++){
                    	System.out.println(customerRentals.get(i).toString());
                    }
                    
                    // Gets user input
                    int cancel = scanner.nextInt(); 
                    
                    
                    // Deletes the rental from the table completely
                    PreparedStatement cancelRental = connection.prepareStatement("DELETE FROM Rental where id = ?");
                    cancelRental.setInt(1, cancel);
                    cancelRental.executeQuery();
                    
                /*
                * Make a return on a user rental
                * Covers transaction b
                * READY FOR TESTING
                */    
                }else if (command == MAKE_RETURN){
                	
                	// Queries up outstanding rentals for user and puts them in a list
                	PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rental r where r.customer_id = ? AND r.checked_out = true");
                	preparedStatement.setInt(1, customer.id);
                	List<Rental> outstandingRentals = Rental.createListFromResultSet(preparedStatement.executeQuery());
                	
                	System.out.println("Please select from the following outstanding rentals to return by typing the id number associated with the rental you wish to return.");
                	// Prints outstanding rentals
                	for (int i = 0; i < outstandingRentals.size(); i++){
                		System.out.println(outstandingRentals.get(i).toString());
                	}
                	
                	int ret = scanner.nextInt();
                	
                	PreparedStatement Return = connection.prepareStatement("UPDATE RENTAL r SET return_date = ? AND checked_out = false WHERE r.id = ?");
                	Return.setDate(1, currentdate);
                	Return.setInt(2, ret);
                	Rental returnedRental = Rental.createFromResultSetRow(Return.executeQuery());
                	Return.executeQuery();
                	
                	int daysdiff = currentdate.getDate() - returnedRental.dueDate.getDayOfMonth();
                	float bikeCost = returnedRental.getBicycle(connection).costPerDay;
                	float refund = daysdiff * bikeCost;
                	
                	System.out.println("Total refund is " + Float.toString(refund));
                	
                }else{
                	System.out.println("Not a valid menu command. Please re-enter command option: ");
                	command = Integer.parseInt(scanner.nextLine());
                }
            }

        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
            e.printStackTrace();
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

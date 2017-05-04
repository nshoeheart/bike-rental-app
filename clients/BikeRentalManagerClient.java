package clients;

import models.Bicycle;
import models.BikeCondition;
import models.Customer;
import models.Rental;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;

import models.Rental;

/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalManagerClient {
	
	private static final int EXIT = 0;
	private static final int VIEW_CURRENT_RENTS = 1; // Scenario covers query a
	private static final int PROCESS_CHECKOUT = 2; // Scenario covers transaction a
	private static final int PROCESS_RETURN = 3; // Scenario covers transaction b
	private static final int VIEW_CONDITIONS = 4; // Scenario covers query e
	private static final int EXECUTE_SERVICE = 5; // Scenario covers transaction e

    public BikeRentalManagerClient() {
    }

    public void run() {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
        	
            connection = ConnectionManager.getConnection();
            
            int command = 1;
            
            while (command != 0) {
            	
                //Menu for commands, manager must enter 0 to exit app
                System.out.print("Please enter the number of the command you wish to do:\n  0. Logout\n  1. View All Current Rentals\n  "
                        + "2. Process Bike Checkout\n  3. Process Rental Return\n  4. View Bike Conditions\n  5. Schedule and Perform Service\n"); 
                
                
                command = Integer.parseInt(scanner.nextLine());

                if (command == EXIT) {
                    // Logs out and ends application
                    System.out.println("Logging out...");
                    
                /*
                 * Managers view of all rentals 
                 * Covers query a
                 * 
                 */
                	
                } else if (command == VIEW_CURRENT_RENTS) {
                	
                    menuOption1();

                } else if (command == PROCESS_CHECKOUT) {
                	
                	System.out.println("What is the customer's id number that is checking out?");
                	int customer_id = scanner.nextInt();
                	System.out.println("What is the bike's id number that the customer wishes to checkout?");
                	int bike_id = scanner.nextInt();
                    
                	menuOption2(customer_id, bike_id);

                } else if (command == PROCESS_RETURN) {
                	
                	System.out.println("What is the rental id number that is being returned?");
                	int rental_id = scanner.nextInt();
                	
                	menuOption3(rental_id);
                	
                } else if (command == VIEW_CONDITIONS){
                	
                	menuOption4();
                	
                } else if (command == EXECUTE_SERVICE){
                	
                	System.out.println("What is the bike id of the bike being serviced?");
                	int bike_id = scanner.nextInt();
                	System.out.println("What is the service id of the service beind performed?");
                	int service_id = scanner.nextInt();
                	
                	menuOption5(bike_id, service_id);
                }
            }
            //todo insert manager client code here
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
            e.printStackTrace();
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
    
    // Allows manager to see all rentals
    private void menuOption1(){
    	Connection connection = null;
    	
    	try{
    		
    		PreparedStatement statement = connection.prepareStatement("SELECT * FROM Rental");
            List<Rental> Rentals =  Rental.createListFromResultSet(statement.executeQuery());
            
            for(int i = 0; i < Rentals.size(); i++){
            	System.out.println(Rentals.get(i).toString());
            }
    		// If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
    
    
    // Allows a manager, assuming he knows the customer and bike id's of the rental
    private void menuOption2(int customer_id, int bike_id){
    	Connection connection = null;
    	
    	try{
    		
    		connection = ConnectionManager.getConnection();
    		PreparedStatement update = connection.prepareStatement("UPDATE Rental r SET checked_out = true WHERE r.bikeId = ? AND r.customerId = ?");
    		update.setInt(1, bike_id);
    		update.setInt(2, customer_id);
    		update.executeUpdate();
    		
    		// If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
    
    
    // Allows manager to process a return, assuming he knows the rental id that is to be returned
    private void menuOption3(int rental_id){
    	Connection connection = null;
    	
    	try{
    		
    		connection = ConnectionManager.getConnection();
    		PreparedStatement update = connection.prepareStatement("UPDATE Rental r SET checked_out = false AND return_date = ? WHERE r.id = ?");
    		update.setDate(1, Date.valueOf(LocalDate.now()));
    		update.setInt(2, rental_id);
    		update.executeUpdate();
    		
    		// If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
    
    // Returns all bikes and their conditions: New, Good, etc.
    private void menuOption4(){
    	Connection connection = null;
    	
    	try{
    		
    		connection = ConnectionManager.getConnection();
    		PreparedStatement conditions = connection.prepareStatement("SELECT Bicycle.id, Bicycle.model, Bicycle.make, BikeCondition.name FROM Bicycle JOIN BikeCondition ON Bicycle.bikeConditionId = Condition.id");
    		List<BikeCondition> bikeConditions = BikeCondition.createListFromResultSet(conditions.executeQuery());
    		System.out.println("Bicycles and their Conditions:\n");
    		
    		for(int i = 0; i < bikeConditions.size(); i++){
    			System.out.println(bikeConditions.get(i));
    		}
    		
    		// If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
    
    // Performs selected service on selected bike on today's date
    private void menuOption5(int bike_id, int service_id){
    	Connection connection = null;
    	
    	try{
    		
    		connection = ConnectionManager.getConnection();
    		PreparedStatement performService = connection.prepareStatement("INSERT INTO PerformedService (offeredServiceId, bikeId, datePerformed) VALUES (? ? ?)");
    		performService.setInt(1, service_id);
    		performService.setInt(2, bike_id);
    		performService.setDate(3, Date.valueOf(LocalDate.now()));
    		performService.executeUpdate();
    		
    		// If successful, commit transaction (otherwise should not reach this point)
            connection.commit();
        } catch (SQLException e) {
            ConnectionManager.rollbackConnection(connection);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

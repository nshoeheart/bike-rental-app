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
                 * READY FOR TESTING
                 */
                	
                } else if (command == VIEW_CURRENT_RENTS) {
                	
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Rental");
                    List<Rental> Rentals =  Rental.createListFromResultSet(statement.executeQuery());
                    
                    for(int i = 0; i < Rentals.size(); i++){
                    	System.out.println(Rentals.get(i).toString());
                    }

                } else if (command == PROCESS_CHECKOUT) {
                    

                } else if (command == PROCESS_RETURN) {

                	
                } else if (command == VIEW_CONDITIONS){

                	
                } else if (command == EXECUTE_SERVICE){
                
                	
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
}

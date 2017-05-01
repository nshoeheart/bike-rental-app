package clients;

import util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;


/**
 * Created by Nathan on 4/26/17.
 */
public class BikeRentalCustomerClient {

    private String name;
    private int id;

    public BikeRentalCustomerClient() {
    	
    }
    
    public BikeRentalCustomerClient(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void run() {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = ConnectionManager.getConnection();
            
            // User input command, checked each loop
            int command = 1;
            
            while (command != 0){
            	
            	//Menu for commands, customer must enter 0 to exit app
            	System.out.print("Please enter the number of the command you wish to do:\n  0. Logout\n  1. Rent Bike\n  "
            			+ "2. Browse Equipment\n  3. Request a Service\n"); // 1. will enter into rentals table, 2 into equipment browser, 3 allows for services to be requested
            	
            	//Check user input
            	command = Integer.parseInt(scanner.nextLine());
            	
            	if(command == 0){
            		
            		//Logs out and ends application
            		System.out.print("Logging out.\n");
            		break;
            		
            	}else if(command == 1){
            		
            		//Links to Rental table and returns all bikes that can be rented out
            		int rent = 0;            		
            		
            		// Allows user to query to DB based on commands they select
            		Statement statement1 = (Statement) connection.createStatement();
            		ResultSet result1 = statement1.executeQuery("select * from Rental where checked_out = N"); // Returns in result all bikes on rental table available for rental
            		System.out.print("Please select one of the following bikes to rent by entering the id associated with the bike; "
            				+ "\n type '999' to go back to main menu:\n");
            		
            		// Prints each result from Rental table that are available for customer to rent
            		while(result1.next()){
            			System.out.print(result1.toString() + "\n"); 
            			
            		}
            		
            		rent = Integer.parseInt(scanner.nextLine());
            		
            		if (rent != 999){ //Case where bike is rented, then we need to update the rental table
            			try{
            				// If bike is rented, attempt to update it in database to have rented status
            				PreparedStatement rentBike = (PreparedStatement) connection.prepareStatement("update Rental set checked_out = Y where Rental.id = " + String.valueOf(rent)); //queries and updates table approrpiately
            				rentBike.executeUpdate();
            				
            			} catch (Exception e){
            				System.out.print("Failed to rent the requested bike.");
            				
            			}
            		}
            		
            		
            	}else if(command == 2){
            		
            		// Allows user to shop equipment
            		int select = 0;
            		
            		// Allows user to query to DB based on commands they select
            		Statement statement2 = (Statement) connection.createStatement();
            		ResultSet result2 = statement2.executeQuery("select * from Equpiment");
            		System.out.print("Please browse the equipment available for purchase. If you wish to buy one, please type the id associated with it or 999 to exit:\n");
            		
            		// Prints out all available equipment from resulting query
            		while(result2.next()){
            			System.out.print(result2.toString() + "\n");            			
            		}
            		
            		// Read user input
            		select = Integer.parseInt(scanner.nextLine());
            		
            		if(select == 999){}; // no updates/insert necessary for this if we assume we will always keep items in stock
            		
            		
            	}else if(command == 3){ 
            		
            		// Request a service on a customer bike
            		int service = 0;
            		
            		// Allows user to query DB based on input commands
            		Statement statement3 = (Statement) connection.createStatement();
            		ResultSet result3 = statement3.executeQuery("select * from OfferedService"); //Queues up all the available services shop can perform on bike
            		System.out.print("Please select one of the following services to perform on your bike:\n");
            		
            		//Prints all available services from OfferedService table so customer can choose what they need
            		while (result3.next()){
            			System.out.print(result3.toString() + "\n");            			
            		}
            		
            		// Read user input
            		service = Integer.parseInt(scanner.nextLine());
            		
            		// If service selected, asks customer about bike to prepare it for insertion into DB
            		if(service != 999){ 
            			
            			System.out.print("Please answer the following: \n What make of bike?");
                		String make = scanner.nextLine();
                		System.out.print("\n What model of bike?");
                		String model = scanner.nextLine();
                		System.out.println("\n Describe the bike's condition by one of the following words: Poor, Fair, Good, Great, New");
                		String condition = scanner.nextLine();
                		
                		int b_id = 0; // I use this to get the correct bike id when we add a new one
                		
                		try{     
                			// Set up a new query
                			Statement statement4 = (Statement) connection.createStatement();
                    		ResultSet result4 = statement4.executeQuery("select count(*) from Bicycle"); //gives total count of bikes
                    		// This returns the next bike ID
                    		b_id = result4.getInt(1) - 1;
                    		
                    		// Adds customer bike to Bicycle table for reference and performs the requested service 
                    		PreparedStatement addBike = (PreparedStatement) connection.prepareStatement("insert into Bicycle values (" + b_id + ", " + make + ", " + model + ", " + condition + ", NULL"); 
                    		addBike.execute();	
                    		PreparedStatement performService = (PreparedStatement) connection.prepareStatement("insert into PerformedService values (" + b_id + ", NULL");
                    		performService.execute();
                    		
                		}catch (Exception e){
                			System.out.print("Failed to add bike and perform your requested service. Please try again.");
                		}
            		
            		}
            	}
            }
   
        } catch (SQLException e) {
            //print error?
        } finally {
            ConnectionManager.closeConnection(connection); //Ends client application
        }
    }
}

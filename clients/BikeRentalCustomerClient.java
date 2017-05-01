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
            			+ "2. Browse Equipment\n  3. MORE COMMANDS\n"); // 1. will enter into rentals table, 2 into services table etc.
            	
            	//Check user input
            	command = Integer.parseInt(scanner.nextLine());
            	
            	if(command == 0){
            		//Logs out and ends application
            		System.out.print("Logging out.\n");
            		break;
            	}
            	else if(command == 1){
            		int rent = 1;
            		//Links to Rental table and returns all bikes that can be rented out
            		int i = 1; // count to associate each bike
            		
            		Statement statement1 = (Statement) connection.createStatement();
            		ResultSet result1 = statement1.executeQuery("select * from Rental where checked_out = N"); // Returns in result all bikes on rental table available for rental
            		System.out.print("Please select one of the following bikes to rent by entering the number on the left of each bike; "
            				+ "\n type '0' to go back to main menu:\n");
            		
            		while(result1.next()){
            			System.out.print("code reached");
            			System.out.print("  " + i + " " + result1.toString() + "\n");
            			i++;
            		}
            		
            		rent = Integer.parseInt(scanner.nextLine());
            		
            		if (rent != 0){ //Case where bike is rented, then we need to update the rental table
            			try{
            				// If bike is rented, attempt to update it in database to have rented status
            				PreparedStatement rentBike = (PreparedStatement) connection.prepareStatement("update Rental set checked_out = Y"); //queries and updates table approrpiately
            				rentBike.executeUpdate();
            				
            			} catch (Exception e){
            				System.out.print("Not a valid input; Returning you to main menu.");
            				
            			}
            		}
            		
            		
            	}
            	else if(command == 2){
            		//link into services table to request a service on bike
            		int select = 1;
            		
            		int j = 1;
            		Statement statement2 = (Statement) connection.createStatement();
            		ResultSet result2 = statement2.executeQuery("select * from Equpiment");
            		System.out.print("Please browse the equipment available for purchase. If you wish to buy one, please type the id associated with it or 0 to exit:\n");
            		
            		while(result2.next()){
            			System.out.print(" " + j + " " + result2.toString() + "\n");
            			j++;
            		}
            		
            		select = Integer.parseInt(scanner.nextLine());
            		if(select == 0){};
            		
            		
            	}else if(command == 3){
            		
            	}
            }
            
            
            //todo insert customer client code here
        } catch (SQLException e) {
            //print error?
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

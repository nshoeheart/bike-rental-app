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
public class BikeRentalManagerClient {

    public BikeRentalManagerClient() {}

    public void run() {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);
       
        try {
            connection = ConnectionManager.getConnection();
            int command = 1;
            while (command != 0){
            	//Menu for commands, manager must enter 0 to exit app
            	System.out.print("Please enter the number of the command you wish to do:\n  0. Logout\n  1. Add a Bike to Inventory\n  "
            			+ "2. Add a new rental record\n  3. MORE COMMANDS\n"); // 1. will enter into bicycle table, 2 into rental table etc.
            	command = Integer.parseInt(scanner.nextLine());
            	
            	if(command == 0){
            		//Logs out and ends application
            		break;
            	
            	}else if(command == 1){
            		//link into bicycle table to add the user input bike
            		System.out.print("Adding A Bike To Inventory\n\n Answer the following questions:\n");
            		// Asks to establish bike we need to add
            		System.out.print("Type 'exit' to cancel bike addition. \nWhat make is the bike?\n");
            		String make = scanner.nextLine();
            		System.out.print("What model is the bike?\n");
            		String model = scanner.nextLine();
            		System.out.print("What condition is the bike? Please choose from the following words: Poor, Fair, Good, Great, New\n");
            		String condition = scanner.nextLine();
            		System.out.print("How much does it cost per day to rent?\n");
            		Float cost = Float.valueOf(scanner.nextLine());
            		
            		Statement statement1 = (Statement) connection.createStatement();
            		ResultSet result1 = statement1.executeQuery("select * from Bicycle");
            		
            		//Inserts the user input bike into the Bicycle Table
            		PreparedStatement addBike = (PreparedStatement) connection.prepareStatement("insert into Bicycle values (" + make + ", " + model + ", " + condition + ", " +  cost.toString() + ")");

            	}else if(command == 2){
            		//link into services to see what services shop still needs to perform
            		
            		System.out.print("Adding A New Rental Record\n\n Answer the following questions regarding the rental:\n Type 'exit' to cancel rental addition.\n");
            		// Asks to establish rental record
            		System.out.print("What is bike id?\n");
            		int b_id = Integer.parseInt(scanner.nextLine());
            		System.out.print("What is the customer's id number?\n");
            		int c_id = Integer.parseInt(scanner.nextLine());
            		System.out.print("What was the checkout date? Please format like : YYYY-MM-DD (please include hyphens in your response)\n");
            		String checkout_date = scanner.nextLine();
            		System.out.print("What is the due date? Please format like : YYYYMMDD (please include hyphens in your response)\n");
            		String due_date = scanner.nextLine();
            		System.out.print("What was the date it was returned? Please format like YYYYMMDD (please include hyphens in your response)\n If not yet returned, type 'NULL'");
            		String return_date = scanner.nextLine();
            		String checked_out = "N";
            		if(return_date == "NULL"){checked_out = "Y";} // this checks to see if bike is returned, sets 'checked_out' variable accordingly

            		Statement statement2 = (Statement) connection.createStatement();
            		ResultSet result2 = statement2.executeQuery("select * from Rental");
            		
					//Inserts the user input rental into Rental Table
            		PreparedStatement addRental = (PreparedStatement) connection.prepareStatement("insert into Rental values (" + Integer.toString(b_id) + ", " + Integer.toString(c_id) + ", " + checkout_date + ", "
            				+ due_date + ", " + return_date + ", " + checked_out);
            		
            	}else if(command == 3){
            		
            	}
            }
            //todo insert manager client code here
        } catch (SQLException e) {
            //print error?
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}

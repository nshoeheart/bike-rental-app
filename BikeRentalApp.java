import clients.BikeRentalCustomerClient;
import clients.BikeRentalManagerClient;
import util.Bootstrap;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nathan on 4/17/17.
 */
public class BikeRentalApp {
    public static void main(String[] args) {

        List<String> cmdArgs = Arrays.asList(args);

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

        if (cmdArgs.contains("manager") && cmdArgs.contains("customer")) {
            System.out.println("Please select either manager or customer");
            System.exit(0);
        }

        if (cmdArgs.contains("manager")) {
            new BikeRentalManagerClient().run();
        }

        if (cmdArgs.contains("customer")) {
            Scanner scanner = new Scanner(System.in);
            String name = "";

            while (name.isEmpty()) {
                System.out.print("Customer Name: ");
                name = scanner.nextLine();
            }

            new BikeRentalCustomerClient(name).run();
        }
    }
}

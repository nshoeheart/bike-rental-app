package models;

import java.time.LocalDate;

/**
 * Created by Nathan on 4/17/17.
 */
public class Rental {
    private int id;
    private int bikeId;
    private int customerId;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean checkedOut;
}

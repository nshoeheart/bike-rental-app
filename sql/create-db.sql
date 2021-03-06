-- Drop statements

DROP TABLE IF EXISTS ServicePlan;
DROP TABLE IF EXISTS PerformedService;
DROP TABLE IF EXISTS Rental;
DROP TABLE IF EXISTS Bicycle;
DROP TABLE IF EXISTS OfferedService;
DROP TABLE IF EXISTS BikeCondition;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Equipment;


-- Create table statements

CREATE TABLE Equipment (
    id INT NOT NULL AUTO_INCREMENT,
    brand VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    cost FLOAT NOT NULL,
    stock INT NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE Customer (
    id INT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE BikeCondition (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE OfferedService (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    cost FLOAT NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE Bicycle (
    id INT NOT NULL AUTO_INCREMENT,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    cost_per_day FLOAT NOT NULL,
    bike_condition_id INT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (bike_condition_id) REFERENCES BikeCondition(id) ON DELETE CASCADE
);

CREATE TABLE Rental (
    id INT NOT NULL AUTO_INCREMENT,
    bike_id INT NOT NULL,
    customer_id INT NOT NULL,
    checkout_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    checked_out BOOLEAN NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (bike_id) REFERENCES Bicycle(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES Customer(id) ON DELETE CASCADE
);

CREATE TABLE PerformedService (
    id INT NOT NULL AUTO_INCREMENT,
    offered_service_id INT NOT NULL,
    bike_id INT NOT NULL,
    date_performed DATE NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (offered_service_id) REFERENCES OfferedService(id) ON DELETE CASCADE,
    FOREIGN KEY (bike_id) REFERENCES Bicycle(id) ON DELETE CASCADE
);

CREATE TABLE ServicePlan (
    bike_condition_id INT NOT NULL,
    offered_service_id INT NOT NULL,

    PRIMARY KEY (bike_condition_id, offered_service_id),
    FOREIGN KEY (bike_condition_id) REFERENCES BikeCondition(id) ON DELETE CASCADE,
    FOREIGN KEY (offered_service_id) REFERENCES OfferedService(id) ON DELETE CASCADE
);
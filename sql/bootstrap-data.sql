-- Add insert statements for initial database data here

INSERT INTO Equipment
    (brand, name, cost, stock)
VALUES
    ('Giro', 'Helmet', 35.00, 100),
    ('ATD', 'Gloves', 15.99, 25),
	('Oakley', 'Gloves', 24.99, 30);


INSERT INTO Customer
    (last_name, first_name)
VALUES
    ('Bergquist', 'Alex'),
    ('Schuchert', 'Nathan'),
	('Doe', 'John'),
	('Smith', 'Jane');


INSERT INTO BikeCondition
    (name)
VALUES
    ('New'),
    ('Great'),
    ('Good'),
    ('Fair'),
    ('Worn'),
    ('Totaled');


INSERT INTO OfferedService
    (name, cost)
VALUES
    ('Safety Check', 25),
    ('Change/Repair Tire', 10),
	('Adjust Brakes', 10),
	('Tune Up', 85);


INSERT INTO Bicycle
    (make, model, cost_per_day, bike_condition_id)
VALUES
    ('Trek', 'Lexa 4 Road Bike', 50.00, 2),
    ('Trek', 'Doman ALR 4 Road Bike', 35.00, 3),
	('S-Works', 'Ruby eTap', 80.00, 1),
	('Hardrock', '650b', 25, 4);


INSERT INTO Rental
    (bike_id, customer_id, checkout_date, due_date, return_date, checked_out)
VALUES
    (1, 1, '2017-03-30', '2017-04-06', NULL, true),
    (2, 2, '2017-03-16', '2017-04-02', '2017-04-02', false),
	(3, 3, '2017-03-30', '2017-04-02', '2017-04-01', false),
	(4, 4, '2017-05-03', '2017-05-05', NULL, true);


INSERT INTO PerformedService
    (offered_service_id, bike_id, date_performed)
VALUES
    (1, 1, '2017-03-30'),
    (2, 4, '2017-03-30'),
	(4, 4, '2017-03-31');


INSERT INTO ServicePlan
    (bike_condition_id, offered_service_id)
VALUES
    (1, 1),
    (1, 2),
	(1, 3),
	(2, 1),
	(2, 3),
	(3, 4),
	(4, 2),
	(4, 3);

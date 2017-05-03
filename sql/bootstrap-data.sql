-- Add insert statements for initial database data here

INSERT INTO Equipment
    (brand, name, cost, stock)
VALUES
    ( , , , ),
    ( , , , );


INSERT INTO Customer
    (last_name, first_name)
VALUES
    ( , ),
    ( , );


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
    ( , ),
    ( , );


INSERT INTO Bicycle
    (make, model, cost_per_day, bike_condition_id)
VALUES
    ( , , , ),
    ( , , , );


INSERT INTO Rental
    (bike_id, customer_id, checkout_date, due_date, return_date, checked_out)
VALUES
    ( , , , , , ),
    ( , , , , , );


INSERT INTO PerformedService
    (offered_service, bike_id, date_performed)
VALUES
    ( , , ),
    ( , , );


INSERT INTO ServicePlan
    (bike_condition_id, offered_service_id)
VALUES
    ( , ),
    ( , );
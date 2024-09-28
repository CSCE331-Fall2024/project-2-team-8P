from uuid import uuid4
# Employee Data Structure (isManager, Name)
employees = [
    (uuid4(), True, "Ash"),
    (uuid4(), True, "Pikachu"),
    (uuid4(), False, "Charizard"),
    (uuid4(), False, "Greninja"),
    (uuid4(), False, "Jigglypuff"),
    (uuid4(), False, "Snorlax"),
    (uuid4(), False, "Mewtwo"),
    (uuid4(), False, "Gengar"),
    (uuid4(), False, "Magikarp"),
    (uuid4(), False, "Ditto")
]

cashiers = employees[2:]

# Menu Item Datastructure (UUID, price, availableStock, itemName)
drinks = {
    (uuid4(), 2.1, 451, 'Dr. Pepper'), 
    (uuid4(), 2.1, 835, 'Aquafina'), 
    (uuid4(), 2.1, 701, 'Sweet Tea'), 
    (uuid4(), 2.1, 529, 'Pepsi')
}

sides = {
    (uuid4(), 4.4, 382, 'Chow Mein'),
    (uuid4(), 4.4, 980, 'Fried Rice'), 
    (uuid4(), 4.4, 453, 'White Steamed Rice'), 
    (uuid4(), 4.4, 647, 'Super Greens')
    
}
entrees = [
    (uuid4(), 5.2, 912, 'Hot Ones Blazing Bourbon Chicken'), 
    (uuid4(), 5.2, 365, 'The Original Orange Chicken'), 
    (uuid4(), 6.7, 761, 'Black Pepper Sirloin Steak'), 
    (uuid4(), 6.7, 604, 'Honey Walnut Shrimp'), 
    (uuid4(), 5.2, 536, 'Grilled Teriyaki Shrimp'), 
    (uuid4(), 5.2, 245, 'Broccoli Beef'), 
    (uuid4(), 5.2, 412, 'Kung Pao Chicken'), 
    (uuid4(), 5.2, 611, 'Honey Sesame Chicken Breast'), 
    (uuid4(), 5.2, 133, 'Beijing Beef'), 
    (uuid4(), 5.2, 763, 'Black Pepper Chicken'), 
]

appetizers = {
    (uuid4(), 2.0, 279, 'Cream Cheese Rangoon'), 
    (uuid4(), 2.0, 345, 'Chicken Egg Roll')
}


# inventory items
inventory_items = [
    (uuid4(), 0.10, 1000, 'Napkin'),
    (uuid4(), 0.15, 800, 'Utensil'),
    (uuid4(), 0.05, 500, 'Fortune Cookie'),
    (uuid4(), 0.50, 200, 'Cup'),
    (uuid4(), 0.10, 300, 'Lid'),
    (uuid4(), 0.10, 500, 'Straw'),
    (uuid4(), 1.50, 100, 'White Rice'),
    (uuid4(), 0.25, 1000, 'Soy Sauce'),
    (uuid4(), 0.30, 700, 'Teriyaki Sauce'),
    (uuid4(), 0.50, 300, 'Bourbon Sauce'),
    (uuid4(), 0.60, 300, 'Orange Sauce'),
    (uuid4(), 0.50, 200, 'Honey Sauce'),
    (uuid4(), 3.00, 150, 'Chicken'),
    (uuid4(), 4.00, 120, 'Beef'),
    (uuid4(), 5.00, 100, 'Shrimp'),
    (uuid4(), 0.50, 400, 'Sesame Sauce')
]

# orders (orderid, cashierid, month, day, hour, price)
orders = [
    
]

# month dict mapping

months = {
    9 : 30,
    10 : 31,
    11 : 30,
    12 : 31,
    1 : 31,
    2 : 29,
    3 : 31,
    4 : 30,
    5 : 31,
    6 : 30,
    7 : 31,
    8 : 31,    
}




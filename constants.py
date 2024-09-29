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
drinks = [
    (uuid4(), 2.1, 451, 'Dr. Pepper'), 
    (uuid4(), 2.1, 835, 'Aquafina'), 
    (uuid4(), 2.1, 701, 'Sweet Tea'), 
    (uuid4(), 2.1, 529, 'Pepsi')
]

sides = [
    (uuid4(), 4.4, 382, 'Chow Mein'),
    (uuid4(), 4.4, 980, 'Fried Rice'), 
    (uuid4(), 4.4, 453, 'White Steamed Rice'), 
    (uuid4(), 4.4, 647, 'Super Greens')
]    

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

appetizers = [
    (uuid4(), 2.0, 279, 'Cream Cheese Rangoon'), 
    (uuid4(), 2.0, 345, 'Chicken Egg Roll')
]


# inventory items
inventory_items = {
   # With each order
   "Napkin": (uuid4(), 0.10, 1000, 'Napkin'),
   "Utensil": (uuid4(), 0.15, 800, 'Utensil'),
   "Fortune Cookie": (uuid4(), 0.05, 500, 'Fortune Cookie'),
   # With drinks
   "Cup": (uuid4(), 0.50, 200, 'Cup'),
   "Lid": (uuid4(), 0.10, 300, 'Lid'),
   "Straw": (uuid4(), 0.10, 500, 'Straw'),
   # Ingredients
   "White Rice": (uuid4(), 1.50, 100, 'White Rice'),
   "Soy Sauce": (uuid4(), 0.25, 1000, 'Soy Sauce'),
   "Teriyaki Sauce": (uuid4(), 0.30, 700, 'Teriyaki Sauce'),
   "Bourbon Sauce": (uuid4(), 0.50, 300, 'Bourbon Sauce'),
   "Orange Sauce": (uuid4(), 0.60, 300, 'Orange Sauce'),
   "Honey Sauce": (uuid4(), 0.50, 200, 'Honey Sauce'),
   "Chicken": (uuid4(), 3.00, 150, 'Chicken'),
   "Beef": (uuid4(), 4.00, 120, 'Beef'),
   "Shrimp": (uuid4(), 5.00, 100, 'Shrimp'),
   "Sesame Sauce": (uuid4(), 0.50, 400, 'Sesame Sauce'),
   "Prepackaged noodles": (uuid4(), 1.5, 1, "Prepackaged noodles"),
   "Sesame Oil": (uuid4(), 0.2, 1, "Sesame oil"),
   "Eggs": (uuid4(), 0.2, 2, "Eggs"),
   "Vegetable mix": (uuid4(), 0.5, 1, "Vegetable mix"),
   "Broccoli": (uuid4(), 0.8, 1, "Broccoli"),
   "Kale": (uuid4(), 0.6, 1, "Kale"),
   "Cabbage": (uuid4(), 0.5, 1, "Cabbage"),
   "Olive oil": (uuid4(), 0.3, 1, "Olive oil"),
   "Black pepper sauce": (uuid4(), 0.7, 1, "Black pepper sauce"),
   "Hot sauce": (uuid4(), 0.3, 1, "Hot sauce"),
   "Oyster sauce": (uuid4(), 0.7, 1, "Oyster sauce"),
   "Kung Pao sauce": (uuid4(), 0.7, 1, "Kung Pao sauce"),
   "Peanuts": (uuid4(), 0.6, 1, "Peanuts"),
   "Candied walnuts": (uuid4(), 0.9, 1, "Candied walnuts"),
   "Tempura batter": (uuid4(), 0.6, 1, "Tempura batter"),
   "Beijing sauce": (uuid4(), 0.8, 1, "Beijing sauce"),
   "Cream Cheese Rangoon": (uuid4(), 2.0, 279, 'Cream Cheese Rangoon'), 
   "Chicken Egg Roll" : (uuid4(), 2.0, 345, 'Chicken Egg Roll'),
   
}

specific_items = {
   "All Orders": [
       inventory_items["Napkin"],
       inventory_items["Utensil"],
       inventory_items["Fortune Cookie"]
   ],
   "Drink": [
       inventory_items["Cup"],
       inventory_items["Lid"], 
       inventory_items["Straw"]
   ],
   "Cream Cheese Rangoon": [
       inventory_items["Cream Cheese Rangoon"]
   ],
   "Chicken Egg Roll":[
       inventory_items["Chicken Egg Roll"]
    ],
   "White Steamed Rice" : [
     inventory_items["White Rice"]  
   ],
   "Chow Mein": [
       inventory_items["Prepackaged noodles"],
       inventory_items["Soy Sauce"],
       inventory_items["Sesame Oil"]
   ],
   "Fried Rice": [
       inventory_items["White Rice"],
       inventory_items["Eggs"],
       inventory_items["Vegetable mix"],
       inventory_items["Soy Sauce"],
       inventory_items["Sesame Oil"]
   ],
   "Super Greens": [
       inventory_items["Broccoli"],
       inventory_items["Kale"],
       inventory_items["Cabbage"],
       inventory_items["Olive oil"]
   ],
   "Hot Ones Blazing Bourbon Chicken": [
       inventory_items["Chicken"],
       inventory_items["Bourbon Sauce"],
       inventory_items["Hot sauce"]
   ],
   "The Original Orange Chicken": [
       inventory_items["Chicken"],
       inventory_items["Orange Sauce"],
       inventory_items["Soy Sauce"]
   ],
   "Black Pepper Sirloin Steak": [
       inventory_items["Beef"],
       inventory_items["Black pepper sauce"],
       inventory_items["Soy Sauce"]
   ],
   "Honey Walnut Shrimp": [
       inventory_items["Shrimp"],
       inventory_items["Candied walnuts"],
       inventory_items["Honey Sauce"],
       inventory_items["Tempura batter"]
   ],
   "Grilled Teriyaki Shrimp": [
       inventory_items["Shrimp"],
       inventory_items["Teriyaki Sauce"]
   ],
   "Broccoli Beef": [
       inventory_items["Beef"],
       inventory_items["Broccoli"],
       inventory_items["Soy Sauce"],
       inventory_items["Oyster sauce"]
   ],
   "Kung Pao Chicken": [
       inventory_items["Chicken"],
       inventory_items["Peanuts"],
       inventory_items["Kung Pao sauce"]
   ],
   "Honey Sesame Chicken Breast": [
       inventory_items["Chicken"],
       inventory_items["Honey Sauce"]
   ],
   "Beijing Beef": [
       inventory_items["Beef"],
       inventory_items["Beijing sauce"]
   ],
   "Black Pepper Chicken": [
       inventory_items["Chicken"],
       inventory_items["Black pepper sauce"],
       inventory_items["Soy Sauce"]
   ]
}





# orders (orderid, cashierid, month, day, hour, price)
orders = [
    
]

# order to menu items (orderid, menuitemid, quantity)
order_to_menu = [
    
]

# menu item to inventory item (menuitemid, inventoryitemid, quantity)
menu_to_inventory = [
    
]

# order to inventory item (orderid, inventoryitemid, quantity)
order_to_inventory = [
    
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




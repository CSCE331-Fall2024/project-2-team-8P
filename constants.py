# Employee Data Structure (isManager, Name)
employees = [
    (True, "Ash"),
    (True, "Pikachu"),
    (False, "Charizard"),
    (False, "Greninja"),
    (False, "Jigglypuff"),
    (False, "Snorlax"),
    (False, "Mewtwo"),
    (False, "Gengar"),
    (False, "Magikarp"),
    (False, "Ditto")
]

# Menu Item Datastructure (price, availableStock, itemName)
base_menu_items = [
    (2.1, 451, 'Dr. Pepper'), 
    (2.1, 835, 'Aquafina'), 
    (2.1, 701, 'Sweet Tea'), 
    (2.1, 529, 'Pepsi'), 
    (4.4, 382, 'Chow Mein'), 
    (4.4, 980, 'Fried Rice'), 
    (4.4, 453, 'White Steamed Rice'), 
    (4.4, 647, 'Super Greens'), 
    (5.2, 912, 'Hot Ones Blazing Bourbon Chicken'), 
    (5.2, 365, 'The Original Orange Chicken'), 
    (6.7, 761, 'Black Pepper Sirloin Steak'), 
    (6.7, 604, 'Honey Walnut Shrimp'), 
    (5.2, 536, 'Grilled Teriyaki Shrimp'), 
    (5.2, 245, 'Broccoli Beef'), 
    (5.2, 412, 'Kung Pao Chicken'), 
    (5.2, 611, 'Honey Sesame Chicken Breast'), 
    (5.2, 133, 'Beijing Beef'), 
    (5.2, 763, 'Black Pepper Chicken'), 
    (2.0, 279, 'Cream Cheese Rangoon'), 
    (2.0, 345, 'Chicken Egg Roll')
]
# scale factor 0.86
sf_plate = 0.86
plate_menu_items = base_menu_items[4:18]
plate_menu_items = [(round(item[0]*sf_plate, 2), item[1], item[2] + "(Bowl)") for item in plate_menu_items]

# bowl_menu_items = {
    
# }

# inventory items
inventory_items = [
    (0.10, 1000, 'Napkin'),
    (0.15, 800, 'Utensil'),
    (0.05, 500, 'Fortune Cookie'),
    (0.50, 200, 'Cup'),
    (0.10, 300, 'Lid'),
    (0.10, 500, 'Straw'),
    (1.50, 100, 'White Rice'),
    (0.25, 1000, 'Soy Sauce'),
    (0.30, 700, 'Teriyaki Sauce'),
    (0.50, 300, 'Bourbon Sauce'),
    (0.60, 300, 'Orange Sauce'),
    (0.50, 200, 'Honey Sauce'),
    (3.00, 150, 'Chicken'),
    (4.00, 120, 'Beef'),
    (5.00, 100, 'Shrimp'),
    (0.50, 400, 'Sesame Sauce')
]




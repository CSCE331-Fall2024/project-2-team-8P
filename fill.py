import numpy as np
import matplotlib.pyplot as plt
import random
import psycopg2, psycopg2.sql as sql
from templates import create_tables, insert_tables
import math
# Python script to dynamically populate sql database
# Dictionary for menu items

menu_items = {
    # Added Arbitrary values, can change later if needed
    "sides" : 
    {
        "Chow Mein" : 3,
        "Fried Rice" : 3,
        "White Steamed Rice" : 3,
        "Super Greens" : 3
    },
    "entrees" : 
    {
       "Hot Ones Blazing Bourbon Chicken" : 3.25,
        "The Original Orange Chicken" : 3.25,
        "Black Pepper Sirloin Steak" : 3.5,
        "Honey Walnut Shrimp" : 3.5,
        "Grilled Teriyaki Shrimp" : 3.25,
        "Broccoli Beef" : 3.25,
        "Kung Pao Chicken" : 3.25,
        "Honey Sesame Chicken Breast" : 3.25,
        "Beijing Beef" : 3.25,
        "Black Pepper Chicken" : 3.25,
        
    },
    "appetizers" : {
        "Cream Cheese Rangoon" : 2,
        "Chicken Egg Roll" : 2 
    },

    "drinks" : 
    {
        "Dr. Pepper" : 1,
        "Aquafina" : 1,
        "Sweet Tea" : 1,
        "Pepsi" : 1
    }
   
}

# Generate a random distribution for sales per day for 365 days
np.random.seed(10)
dist_day = np.random.normal(2733.90, 100.65, (365, 1))
sum_sales = np.sum(dist_day)
sum_sales = round(sum_sales, 2)
print("Yearly Sales Sum: ", sum_sales)

# Generate a random number of orders for each day
dist_orders = np.ceil(dist_day/10)
sum_orders = sum(dist_orders)[0]
print("Yearly Orders Sum: ", sum_orders)

# dist_total keeps track of all generated values
dist_total = np.concatenate((dist_day, dist_orders), axis=1)

# Assign an hour to each order 
# Make
np.random.seed(10) 
dist_hour = np.random.randint(1, 13, int(np.sum(dist_orders)))
# print(dist_hour)
    
sum_of_meals = 0
meal_option = 0
counter = 0
sum_day = []
# Assigns j random orders for i days
random.seed(10)
for i in dist_orders:
    sum_order = 0
    for j in range(0, int(i)):
        meal_option = random.randint(1, 5)
        has_drink = random.randint(0, 1)
        if(meal_option == 1): # Bowl
            side = random.choice(list(menu_items["sides"]))
            sum_order += menu_items["sides"][side]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
        elif(meal_option == 2): # Plate
            side = random.choice(list(menu_items["sides"]))
            sum_order += menu_items["sides"][side]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
        elif (meal_option == 3): # Bigger Plate
            side = random.choice(list(menu_items["sides"]))
            sum_order += menu_items["sides"][side]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
        elif (meal_option == 4): # A La Carte (Individual Entrees & Sides)
            entree = random.choice(list(menu_items["entrees"]))
            sum_order += menu_items["entrees"][entree]
            drink = random.choice(list(menu_items["drinks"]))
            sum_order += menu_items["drinks"][drink]

        elif (meal_option == 5): # Appetizers
            appetizer = random.choice(list(menu_items["appetizers"]))
            sum_order += menu_items["appetizers"][appetizer]
            
        if has_drink:
                drink = random.choice(list(menu_items["drinks"]))
                sum_order += menu_items["drinks"][drink]
        counter += 1
    sum_day.append(sum_order)

xs = np.linspace(1, len(sum_day), num=len(sum_day))
plt.plot(xs, sum_day)
plt.show()

print(np.array(sum_day).reshape((len(sum_day), 1)))

# peak_day_1 = random.randint(0, len(dist_orders))
# peak_day_2 = random.randint(0, len(dist_orders))
# print("Peak Day 1: ", peak_day_1)
# print("Peak Day 2: ", peak_day_2)
print("Sum of all meals over all days: ", np.sum(sum_day))
# print("Number of orders: ", counter)


# print(side)
# print(entree)
# print(drink)
# Pick menu items for each order randomly
# Assign cashiers to orders randomly

# Create distribution for sales (day)

# Create distribution for sales (hour)
# np.random.seed(10)
# dist_hour = np.random.normal(229.82, 50, (4380,))
# for i in range(4380):
#     sum += dist_hour[i]
# print(round(sum, 2))



# connect to the local database

# conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
# cur = conn.cursor()

# for table in basetables:
#     cur.execute(table)

# cur.execute(
#     sql.SQL(insert_tables[0]),
#     [False, "Ash"]
# )

# cur.execute(
#     sql.SQL(insert_tables[1]),
#      [1, 3, 5, 7, 9]
# )

# print("Done executing")

# commit changes and close the connection
# conn.commit()

# cur.close()
# conn.close()
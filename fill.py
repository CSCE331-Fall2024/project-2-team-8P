import numpy as np
import random
# import psycopg2, psycopg2.sql as sql
# from templates import create_tables, insert_tables
import math
# Python script to dynamically populate sql database
# Dictionary for menu items

menu_items = {
    "sides" : 
    {
        "Chow Mein" : 2.5,
        "Fried Rice" : 2.5,
        "White Steamed Rice" : 2.5,
        "Super Greens" : 2.5
    },
    "entrees" : 
    {
       "Hot Ones Blazing Bourbon Chicken" : 2.5,
        "The Original Orange Chicken" : 2.5,
        "Black Pepper Sirloin Steak" : 2.5,
        "Honey Walnut Shrimp" : 2.5,
        "Grilled Teriyaki Shrimp" : 2.5,
        "Broccoli Beef" : 2.5,
        "Kung Pao Chicken" : 2.5,
        "Honey Sesame Chicken Breast" : 2.5,
        "Beijing Beef" : 2.5,
        "Black Pepper Chicken" : 2.5,
        "Cream Cheese Rangoon" : 2.5,
        "Chicken Egg Roll" : 2.5 
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
dist_day = np.random.normal(2733.90, 100.65, (365, ))
sum = np.sum(dist_day)
# print("Total Sum: ", round(sum, 2))

# Generate a random number of orders for that day
np.random.seed(10)
dist_orders = np.ceil(dist_day/10)

# print("Orders: ", dist_orders)
# Assign an hour to that order 
# Make 
dist_hour = np.random.randint(1, 13, int(np.sum(dist_orders)))
print(dist_hour)
    
# side = random.choice(list(menu_items))
side = random.choice(list(menu_items["sides"]))
entree = random.choice(list(menu_items["entrees"]))
drink = random.choice(list(menu_items["drinks"]))
print(side)
print(entree)
print(drink)
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
import numpy as np
import psycopg2, psycopg2.sql as sql
from templates import create_tables, insert_tables
import math
# Python script to dynamically populate sql database
# Dictionary for menu items

menu_items = {
    "Chow Mein",
    "Fried Rice",
    "White Steamed Rice",
    "Super Greens",
    "Hot Ones Blazing Bourbon Chicken",
    "The Original Orange Chicken",
    "Black Pepper Sirloin Steak",
    "Honey Walnut Shrimp",
    "Grilled Teriyaki Shrimp",
    "Broccoli Beef",
    "Kung Pao Chicken",
    "Honey Sesame Chicken Breast",
    "Beijing Beef",
    "Black Pepper Chicken",
    "Cream Cheese Rangoon",
    "Chicken Egg Roll",
    "Dr. Pepper",
    "Aquafina",
    "Sweet Tea",
    "Pepsi"
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




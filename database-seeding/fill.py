import numpy as np
import matplotlib.pyplot as plt
import random
import psycopg2, psycopg2.sql as sql
from templates import *
from constants import *
import math
import csv
import pdb

np.random.seed(10)
total_annual_sales = 0

peak_days = {
    # First day of school
    (8, 20),
    # TU game
    (11, 30)
}

# Iterate for 365 days
# iterate over months

week = 1
overall_day = 1
for month in months:
    
    # Generate random number of sales for the current day from a normal distribution
    days_in_month = months[month]
    
    sales_by_day = np.random.normal(2733.90, 100.65, (days_in_month, 1))
    sales_by_day = np.round(sales_by_day, 2)
    total_annual_sales += np.sum(sales_by_day)
    
    # iterate over days in that month
    for day in range(1, days_in_month + 1):
        
        # Iterate while we haven't fufilled today's sales
        curr_day_total_sales = 0
       
        if (month, day) in peak_days:
           curr_day_max_sales = 5000
        else:
           curr_day_max_sales = sales_by_day[day - 1]
       
        while curr_day_total_sales < curr_day_max_sales:
            sale_order = 0 # Total cost of this order
            
            hour = random.randint(1, 12)
            cashier = random.choice(cashiers)
            orderid = uuid4()
            
            # keep track of which menu items we've selected
            menu_items_order = []
            # keep track of which inventory items we've selected
            inventory_items_order = []
            
            # Let's simplify the logic a little:
            # 1. 50% of orders have a drink
            # 2. 50% of orders have a side
            
            # 3. 10% of orders are a-la-carte (only one entree)
            # 3. 50% of orders are bowls
            # 4. 20% of orders are plates
            # 5. 20% of orders are bigger plates
            
            meal_option = random.uniform(0, 1)
            has_drink = random.randint(0, 1)
            has_appetizer = random.randint(0, 1)

            # Let's determine what type of meal the current order contains:
            n_entrees = 0
            has_side = True
            # a la carte entree
            if meal_option < 0.1: 
                n_entrees = 1
                has_side = False
            # bowl
            elif meal_option < 0.6:
                n_entrees = 1
            # plate
            elif meal_option < 0.8: 
                n_entrees = 2
            # bigger plate
            else: 
                n_entrees = 3
                
            for _ in range(n_entrees):
                entree = random.choice(list(entrees))
                
                # Add price of entree to our order
                sale_order += entree[1]
                
                # Add UUID of entree to our order
                menu_items_order.append(entree[0])
                
                # Add inventory items of menu item to our order
                for item in specific_items[entree[3]]:
                    inventory_items_order.append(str(item[0]))
            
            if has_side:
                side = random.choice(list(sides))
                sale_order += side[1]
                menu_items_order.append(side[0])
                # add menu item and inventory item id
                for item in specific_items[side[3]]:
                    inventory_items_order.append(str(item[0]))
                    
            # TODO: add logic to populate the database based on
            # our random item selection here
            if has_appetizer:
                appetizer = random.choice(list(appetizers))
                sale_order += appetizer[1]
                menu_items_order.append(appetizer[0])
                for item in specific_items[appetizer[3]]:
                    inventory_items_order.append(str(item[0]))

            if has_drink:
                drink = random.choice(list(drinks))
                sale_order += drink[1]
                menu_items_order.append(drink[0])
                
                # Add inventory items associated with all drinks to this order
                for item in specific_items["Drink"]:
                    inventory_items_order.append(str(item[0]))
                    
            # appending to list of orders
            # Note: will use this list to add to the table
            orders.append(
                (str(orderid), str(cashier[0]), month, week, day, hour, round(sale_order, 2), "delivered")
            )
            
            # Maps menu items in this order to the current order
            for item in menu_items_order:
                order_to_menu.append((str(orderid), str(item), 1))
            
            # add fortune cookie and other inventory items common to all orders
            for item in specific_items["All Orders"]:
                inventory_items_order.append(str(item[0]))
                
            # menu to inventory items
            unique_inventory_items_order = set(inventory_items_order)
            
            # Count occurrences for each unique inventory item
            for item in unique_inventory_items_order:
                order_to_inventory.append((
                    (str(orderid), item, inventory_items_order.count(item))
                ))
                
            # Add current order's total to current day's total
            curr_day_total_sales += sale_order
            
        if overall_day % 7 == 0:
            week += 1
        overall_day += 1

# add logic for menu item to inventory item
# (orderid, inventoryitemid, quantity)

# Iterate through drinks
for drink in drinks:
    for item in specific_items["Drink"]:
        menu_to_inventory.append(
            (str(drink[0]), str(item[0]), 1)
        )
# Iterate through sides
for side in sides:
    for item in specific_items[side[3]]:
        menu_to_inventory.append(
            (str(side[0]), str(item[0]), 1)
        )
# Iterate through appetizers
for app in appetizers:
    for item in specific_items[app[3]]:
        menu_to_inventory.append(
            (str(app[0]), str(item[0]), 1)
        )
# Iterate through entrees
for ent in entrees:
    for item in specific_items[ent[3]]:
        menu_to_inventory.append(
            (str(ent[0]), str(item[0]), 1)
        )
            
print("Total yearly sales ($): ", total_annual_sales)

# connect to the local database

# print("Connecting to database")
conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
cur = conn.cursor()

# Create all tables
for table in create_tables:
    cur.execute(table)
print("Done Creating Tables")

# Insert items into employee table
for employee in employees:
    cur.execute(
        sql.SQL(i_employee_table),
        [str(employee[0]), employee[1], employee[2]]
)
print("Done Creating Employees")

# Insert base items into the menu items table
for drink in drinks:
    cur.execute(
        sql.SQL(i_menu_item_table),
        [str(drink[0]), drink[1], drink[2], drink[3], drink[4]]
)
print("Done Creating Drinks")

for side in sides:
    cur.execute(
        sql.SQL(i_menu_item_table),
        [str(side[0]), side[1], side[2], side[3], side[4]]
)
print("Done Creating Sides")

for entree in entrees:
    cur.execute(
        sql.SQL(i_menu_item_table),
        [str(entree[0]), entree[1], entree[2], entree[3], entree[4]]
)
print("Done Creating Entrees")
    
for appetizer in appetizers:
        cur.execute(
        sql.SQL(i_menu_item_table),
        [str(appetizer[0]), appetizer[1], appetizer[2], appetizer[3], appetizer[4]]
)
print("Done Creating Appetizers")




# Insert inventory items into inventory items table

for key, item in inventory_items.items():
    cur.execute(
        sql.SQL(i_inventory_item_table),
        [str(item[0]), item[1], item[2], item[3]]
    )
print("Done Creating Inventory Items")

# Insert orders into files to save time (much faster than writing to db)
file = open("orders.csv", 'w')
writer = csv.writer(file)
writer.writerow(["orderid", "cashierid", "month", "week", "day", "hour", "price", "status"])
writer.writerows(orders)
file.close()
print("Done creating orders")

# Insert order to menu item
file  = open("order_to_menu.csv", 'w')
writer = csv.writer(file)
writer.writerow(["orderid", "menuitemid", "quantity"])
writer.writerows(order_to_menu)
file.close()
print("Done creating order to menu items")

# Insert order to inventory items
file = open("order_to_inventory.csv", 'w')
writer = csv.writer(file)
writer.writerow(["orderid", "inventoryitemid", "quantity"])
writer.writerows(order_to_inventory)
file.close()
print("Done creating order to inventory items")

# Insert menu items to inventory items
file = open("menu_to_inventory.csv", 'w')
writer = csv.writer(file)
writer.writerow(["menuitemid", "inventoryitemid", "quantity"])
writer.writerows(menu_to_inventory)
file.close()
print("Done creating menu to inventory")
print("Done Creating Orders")

# commit changes and close the connection
conn.commit()

cur.close()
conn.close()
print("Done executing")

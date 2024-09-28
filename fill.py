import numpy as np
import matplotlib.pyplot as plt
import random
import psycopg2, psycopg2.sql as sql
from templates import *
from constants import *
import math

np.random.seed(10)
total_annual_sales = 0
# Iterate for 365 days
# iterate over months
for month in months:
    # iterate over days in that month
    sales_by_day = np.random.normal(2733.90, 100.65, (months[month], 1))
    sales_by_day = np.round(sales_by_day, 2)
    total_annual_sales += np.sum(sales_by_day)
    for day in range(1, months[month]+1):
        # Iterate while we haven't fufilled today's sales
       total_sales_day = 0
       while total_sales_day < sales_by_day[day-1]:
            sale_order = 0
            hour = random.randint(1, 12)
            cashier = random.choice(cashiers)
            orderid = uuid4()
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
                sale_order += entree[1]
                
            if has_side:
                side = random.choice(list(sides))
                sale_order += entree[1]
            # TODO: add logic to populate the database based on
            # our random item selection here
            if has_appetizer:
                appetizer = random.choice(list(appetizers))
                sale_order += appetizer[1]
            if has_drink:
                drink = random.choice(list(drinks))
                sale_order += drink[1]
            # appending to list of orders
            orders.append(
                (orderid, cashier[0], month, day, hour, round(sale_order, 2))
            )
            # TODO: connection table population 
            total_sales_day += sale_order
            

print("Total yearly sales ($): ", total_annual_sales)

# connect to the local database

# print("Connecting to database")
conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
cur = conn.cursor()

# # Create all tables
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
        [str(drink[0]), drink[1], drink[2], drink[3]]
)
print("Done Creating Drinks")

for side in sides:
        cur.execute(
        sql.SQL(i_menu_item_table),
        [str(side[0]), side[1], side[2], side[3]]
)
print("Done Creating Sides")

for entree in entrees:
    cur.execute(
        sql.SQL(i_menu_item_table),
        [str(entree[0]), entree[1], entree[2], entree[3]]
)
print("Done Creating Entrees")
    
for appetizer in appetizers:
        cur.execute(
        sql.SQL(i_menu_item_table),
        [str(appetizer[0]), appetizer[1], appetizer[2], appetizer[3]]
)
print("Done Creating Appetizers")

# Insert inventory items into inventory items table
for item in inventory_items:
    cur.execute(
        sql.SQL(i_inventory_item_table),
        [str(item[0]), item[1], item[2], item[3]]
    )
print("Done Creating Items")

# Insert orders into database
print("Length of orders: ", len(orders))
for order in orders[:1000]:
    # print("Im infinite")
    cur.execute(
        sql.SQL(i_order_table),
        [str(order[0]), str(order[1]), order[2], order[3], order[4], order[5]]
    )
print("Done Creating Orders")

print("Done executing")

# commit changes and close the connection
conn.commit()

cur.close()
conn.close()
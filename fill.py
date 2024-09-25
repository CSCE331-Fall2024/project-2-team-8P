import psycopg2
# Python script to dynamically populate sql database
# alpha(=52) weeks of sales history
# ALPHA = 52
# beta(=1.00) millions in sales
# BETA = 1000000
# phi(=2) peak sales days
# PHI = 2
# delta(=20) different menu items
# epsilon(=15) different queries
# phi(=4) special queries (weekly sales history, realistic sales history, peak sales days, menu item inventory)


# Create the tables first

c_employee_table = '''
CREATE TABLE employee (
	employeeId SERIAL PRIMARY KEY,
	isManager boolean,
	name VARCHAR(50)
);
'''

c_order_table = '''
CREATE TABLE \"order\" (
	orderId SERIAL PRIMARY KEY,
	cashierID SERIAL,
	CONSTRAINT fk_cashier
		FOREIGN KEY (cashierId)
		 REFERENCES employee(employeeId),
	month INT,
	day INT,
	hour INT, 
	price FLOAT
);
'''

c_menu_item_table = '''
CREATE TABLE menuItem (
	menuItemId SERIAL PRIMARY KEY,
	price FLOAT,
	availableStock INT,
	itemName VARCHAR(100)
);
'''

c_order_to_menu_table = '''
CREATE TABLE orderToMenuItem (
	orderId SERIAL,
	CONSTRAINT fk_order
		FOREIGN KEY (orderId)
			REFERENCES \"order\"(orderId),
	menuItemId SERIAL,
	CONSTRAINT fk_menuItem
		FOREIGN KEY (menuItemId)
			REFERENCES menuItem(menuItemId),
	quantity INT
);
'''

c_inventory_item_table = '''
CREATE TABLE inventoryItem (
	inventoryItemId SERIAL PRIMARY KEY,
	cost FLOAT,
	availableStock INT,
	itemName VARCHAR(80)
);
'''

c_order_to_inventory_table = '''
CREATE TABLE orderToInvetoryItem (
	orderId SERIAL,
	CONSTRAINT fk_order
		FOREIGN KEY (orderId)
			REFERENCES \"order\"(orderId),
	inventoryItemId SERIAL,
	CONSTRAINT fk_iventoryItem
		FOREIGN KEY (inventoryItemId)
			REFERENCES inventoryItem(inventoryItemId),
	quantity INT
);
'''

c_menu_to_inventory_table = '''
CREATE TABLE menuItemToInventoryItem (
	menuItemId SERIAL,
	CONSTRAINT fk_menuItem
		FOREIGN KEY (menuItemId)
			REFERENCES menuItem(menuItemId),
	inventoryItemId SERIAL,
	CONSTRAINT fk_iventoryItem
		FOREIGN KEY (inventoryItemId)
			REFERENCES inventoryItem(inventoryItemId),
	quantity INT
);
'''

# connect to the local database

basetables = [c_employee_table, c_order_table, c_menu_item_table,
              c_order_to_menu_table, c_inventory_item_table, c_order_to_inventory_table,
              c_menu_to_inventory_table]

conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
cur = conn.cursor()

for table in basetables:
    cur.execute(table)

print("Done executing")

# commit changes and close the connection
conn.commit()

cur.close()
conn.close()




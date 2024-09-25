from psycopg2 import sql
# c_ = create
# i_ = insert

### Table Creation
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

#### Table Insertion
i_employee_table = '''
INSERT INTO employee (isManager, name) 

VALUES (
    %s, 
    %s
);
'''
i_order_table = '''
INSERT INTO "order" (cashierID, month, day, hour, price)

VALUES (
	%s,
	%s,
	%s,
	%s,
	%s
);
'''
i_menu_item_table = '''
INSERT INTO menuItem (price, availableStock, itemName)

VALUES (
	%s,
	%s,
	%s
);
'''
i_order_to_menu_item_table = '''
INSERT INTO orderToMenuItem (orderID, menuItemID, quantity)

VALUES (
	%s,
	%s,
	%s
);
'''
i_inventory_item_table = '''
INSERT INTO inventoryItem(cost, availableStock, itemName)

VALUES (
	%s,
	%s,
	%s
);
'''
i_order_to_inventory_item_table = '''
INSERT INTO orderToInventoryItem (orderId, inventoryItemId, quantity)

VALUES (
	%s,
	%s,
	%s
);
'''
i_menu_item_to_inventory_item_table = '''
INSERT INTO menuItemtoInventoryItem (menuItemId, inventoryItemId, quantity)

VALUES (
	%s,
	%s,
	%s
);
'''

create_tables = [c_employee_table, c_order_table, c_menu_item_table,
                c_order_to_menu_table, c_inventory_item_table, c_order_to_inventory_table,
                c_menu_to_inventory_table]
insert_tables = [i_employee_table, i_order_table, i_menu_item_table, 
                 i_order_to_menu_item_table, i_inventory_item_table, i_order_to_inventory_item_table,
                 i_menu_item_to_inventory_item_table]
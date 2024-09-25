import numpy as np
import psycopg2, psycopg2.sql as sql
from templates import create_tables, insert_tables
# Python script to dynamically populate sql database

# Create distribution for sales (day)
# np.random.seed(10)
# dist_day = np.random.normal(2733.90, 100.65, (365, ))
# sum = 0
# for i in range(365):
#     sum += dist[i]
# Create distribution for sales (hour)
# np.random.seed(10)
# dist_hour = np.random.normal(229.82, 50, (4380,))
# for i in range(4380):
#     sum += dist_hour[i]
# print(round(sum, 2))



# connect to the local database

conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
cur = conn.cursor()

# for table in basetables:
#     cur.execute(table)

cur.execute(
    sql.SQL(insert_tables[0]),
    [False, "Ash"]
)

cur.execute(
    sql.SQL(insert_tables[1]),
     [1, 3, 5, 7, 9]
)

print("Done executing")

# commit changes and close the connection
conn.commit()

cur.close()
conn.close()




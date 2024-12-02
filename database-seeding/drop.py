import psycopg2
from constants import *
from templates import *

conn = psycopg2.connect(host="csce-315-db.engr.tamu.edu", dbname="team_8p_db", user="team_8p", password="Electabuzz8!")
cur = conn.cursor()


for table in table_name:
    cur.execute(
        sql.SQL(delete_tables).format(sql.Identifier(table))
    )

cur.execute(
    sql.SQL(delete_types)
)

conn.commit()

cur.close()
conn.close()
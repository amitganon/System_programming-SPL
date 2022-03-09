import atexit
import sqlite3
import sys

import Hat
import Order
import Supplier
from DAO_Object import DAO_Object


class Repository:

    def __init__(self):
        self.conn = sqlite3.connect(sys.argv[4])
        self.hats = DAO_Object(Hat, self.conn)
        self.suppliers = DAO_Object(Supplier, self.conn)
        self.orders = DAO_Object(Order, self.conn)

    def close(self):
        self.conn.commit()
        self.conn.close()

    def create_tables(self):
        self.conn.executescript("""CREATE TABLE suppliers (id INT PRIMARY KEY,name TEXT NOT NULL);
                                    CREATE TABLE hats (id INT PRIMARY KEY, topping TEXT NOT NULL, supplier INT REFERENCES suppliers(id), quantity INT NOT NULL);
                                    CREATE TABLE orders (id INT PRIMARY KEY, location TEXT NOT NULL, hat INT REFERENCES hats(id));""")


repo = Repository()
atexit.register(repo.close)
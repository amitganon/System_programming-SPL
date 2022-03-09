from Hat import Hat
from Supplier import Supplier

class DAO_Object:

    def __init__(self, dto_type, con):
        self._con = con
        self.dto_type = dto_type
        self.table_name = dto_type.__name__.lower() + 's'

    def insert(self, dto_instance):
        ins_dict = vars(dto_instance)
        column_names = ','.join(ins_dict.keys())
        params = ins_dict.values()
        qmarks = ','.join(['?'] * len(ins_dict))

        stmt = 'INSERT INTO {} ({}) VALUES ({})'.format(self.table_name, column_names, qmarks)
        self._con.execute(stmt, list(params))

    def update(self, id, new_val):
        self._con.execute('UPDATE {} SET quantity={} WHERE (id = {})'.format(self.table_name, new_val, id))

    def remove(self, id):
        stmt = 'DELETE FROM {} WHERE (id={})'.format(self.table_name, id)
        self._con.execute(stmt)
    
    def find_by_topping(self, val):
        c = self._con.cursor()
        c.execute("""
                    SELECT * FROM hats WHERE topping = ? AND supplier =(
                    SELECT MIN(supplier) FROM hats WHERE topping = ?)
                """, [val, val])
        return Hat(*c.fetchone())

    def find_supplier(self, id):
        c = self._con.cursor()
        c.execute("""
                    SELECT * FROM suppliers WHERE id = ?
                """, [id])
        return Supplier(*c.fetchone())

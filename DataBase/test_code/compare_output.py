import sys
import numpy as np
import itertools
import sqlite3



def output_check():
    try:
        cmp = np.array([[str(x) for x in(l.strip('\n').split(','))] for l in open(sys.argv[1])]) == np.array([[str(x) for x in(l.strip('\n').split(','))] for l in open(sys.argv[2])])
        [print(f'Mismatch in line {i} value {j}') for i,j in itertools.product(range(cmp.shape[0]),range(cmp.shape[1])) if cmp[i,j] == False]
    except Exception as e:
        print("Failed to compare output files, are they of the same length?")
        return 0
    return np.sum(cmp)/cmp.size


def compare(true_lst, tested_lst,lst_name):
    tested_lst = list(tested_lst)
    mismatches = 0    
    for elem in true_lst:
        try:
            tested_lst.remove(elem)
        except ValueError:
            print(f'Mistake in {lst_name}, no match for: {elem}')
            mismatches+=1
    return (len(true_lst)-mismatches)/len(true_lst)


def compare_hats(db_true,db_tested):
    true_db = db_true.execute("""SELECT id, topping, supplier, quantity FROM hats""").fetchall()
    tested_db = db_tested.execute("""SELECT id, topping, supplier, quantity FROM hats""").fetchall()
    return compare(true_db,tested_db,'hats')

def compare_suppliers(db_true,db_tested):
    true_db = db_true.execute("""SELECT id, name FROM suppliers""").fetchall()
    tested_db = db_tested.execute("""SELECT id, name FROM suppliers""").fetchall()
    return compare(true_db,tested_db,'suppliers')

def compare_orders(db_true,db_tested):
    true_db = db_true.execute("""SELECT id, location, hat FROM orders""").fetchall()
    tested_db = db_tested.execute("""SELECT id, location, hat FROM orders as ord""").fetchall()
    return compare(true_db,tested_db,'orders')


def db_check():
    db_true = sqlite3.connect(sys.argv[3])
    db_tested = sqlite3.connect(sys.argv[4])
    hats_grade = compare_hats(db_true,db_tested)
    print(f'Grade for hats table:{hats_grade}')
    suppliers_grade = compare_suppliers(db_true,db_tested)
    print(f'Grade for suppliers table:{suppliers_grade}')
    orders_grade = compare_orders(db_true,db_tested)
    print(f'Grade for orders table:{orders_grade}')
    return 0.4*hats_grade+0.3*suppliers_grade+0.3*orders_grade

 
if __name__ == '__main__':
    print('OUTPUT FILE TEST')
    output_grade = output_check()
    print(f'Grade for output file:{output_grade}')
    print('\nDB FILE TEST')
    db_grade = db_check()
    print(f'Grade for database file:{db_grade}')
    print(f'Total Grade:{db_grade*0.6+output_grade*0.4}')





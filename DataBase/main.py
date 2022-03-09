import sys

from Order import Order
from Repository import repo
from Hat import Hat
from Supplier import Supplier


def print_hi(name):

    print(f'Hi, {name}')


if __name__ == '__main__':
    config = sys.argv[1]
    orders = sys.argv[2]
    output = sys.argv[3]

    repo.create_tables()

    config_text = open(config, "r+").read()
    orders_text = open(orders, "r+").read()

    s = config_text[0: config_text.find(',')]
    numOfHats = int(s)
    numOfSupp = int(config_text[config_text.find(',')+1 : config_text.find('\n')])
    counter = 0
    for line in config_text.split('\n'):
        split = line.split(',')
        if 0 < counter <= numOfHats:
            hat = Hat(split[0], split[1], split[2], split[3])
            repo.hats.insert(hat)
        elif counter > numOfHats:
            supplier = Supplier(split[0], split[1])
            repo.suppliers.insert(supplier)
        counter += 1

    
    OrderID = 1
    output_text = ""
    for line in orders_text.split('\n'):
        split = line.split(',')
        location = split[0]
        topName = split[1]
        hat = repo.hats.find_by_topping(topName)
        if hat is not None:
            if hat.quantity == 1:
                repo.hats.remove(hat.id)
            else:
                repo.hats.update(hat.id, hat.quantity-1)

        order = Order(OrderID, location, hat.id)
        repo.orders.insert(order)
        OrderID += 1

        supName = repo.suppliers.find_supplier(hat.supplier).name
        output_text += topName + ',' + supName + ',' + location + '\n'

    f = open(output, "w")
    f.write(output_text)
    f.close()










from DTO_Object import DTO_Object


class Hat (DTO_Object):

    def __init__(self, id, topping, supplier, quantity):
        super().__init__(id)
        self.topping = topping
        self.supplier = supplier
        self.quantity = quantity

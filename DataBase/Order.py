from DTO_Object import DTO_Object


class Order (DTO_Object):

    def __init__(self, id, location, hat):
        super().__init__(id)
        self.location = location
        self.hat = hat

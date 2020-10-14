from model.BST import BST


class SymbolTable:

    def __init__(self):
        self.__bst = BST()

    def add(self, symbol):
        self.__bst.insert(symbol)

    def print(self):
        self.__bst.print()

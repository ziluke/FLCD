class Node:

    def __init__(self, symbol, id):
        self.id = id
        self.value = symbol
        self.left = None
        self.right = None


class BST:

    def __init__(self):
        self.count = 0
        self.root = Node("", -1)

    def __add(self, root, node):
        if root is None:
            root = node
            return root.id
        else:
            val = self.search(root, node.value)
            if val == -1:
                if root.value < node.value:
                    if root.right is None:
                        root.right = node
                    else:
                        self.__add(root.right, node)
                else:
                    if root.left is None:
                        root.left = node
                    else:
                        self.__add(root.left, node)
                self.count += 1
                return self.count - 1
            else:
                return val.id

    def insert(self, symbol):
        node = Node(symbol, self.count)
        return self.__add(self.root, node)

    def search(self, root, symbol):
        if root is None or root.value == symbol:
            return root

        if root.value < symbol:
            self.search(root.right, symbol)
        else:
            self.search(root.left, symbol)

        return -1

    def __inOrderTraversal(self, root):
        if root:
            self.__inOrderTraversal(root.left)
            print("\nSymbol: {0} \nCode: {1}", root.value, root.id)
            self.__inOrderTraversal(root.right)

    def print(self):
        self.__inOrderTraversal(self.root)

import re

from model.SymbolTable import SymbolTable


class Scanner:
    def __init__(self):
        self.__separators = [' ', '{', '}', '[', ']', '(', ')', ':', ';', ',', '\'', '\"']
        self.__operators = ['!=', '==', '<', '>', '<=', '>=', '+', '-', '*', '/', '%', '&',
                            '|', '=']
        self.__reservedWords = ['int', 'boolean', 'string', 'char', 'loop', 'if', 'else',
                                'while', 'func', 'read', 'write', 'true', 'false']
        self.__tokens = self.__separators + self.__operators + self.__reservedWords

        self.__codification = dict([(self.__tokens[i], i + 2) for i in range(len(self.__tokens))])
        self.__codification['identifier'] = 0
        self.__codification['constant'] = 1

        self.__st = SymbolTable()
        self.__pif = []

    def isPartOfOperator(self, char):
        for op in self.__operators:
            if char in op:
                return True
        return False

    def getStringToken(self, line, index):
        token = ''
        quoteCount = 0

        while index < len(line) and quoteCount < 2:
            if line[index] == '\"':
                quoteCount += 1
            token += line[index]
            index += 1

        return token, index

    def getOperatorToken(self, line, index):
        token = ''

        while index < len(line) and self.isPartOfOperator(line[index]):
            token += line[index]
            index += 1

        return token, index

    def tokenGenerator(self, line, separators):
        token = ''
        index = 0
        tokens = []
        while index < len(line):
            if line[index] == '"':
                if token:
                    tokens.append(token)
                token, index = self.getStringToken(line, index)
                tokens.append(token)
                token = ''

            elif self.isPartOfOperator(line[index]):
                if token:
                    tokens.append(token)
                token, index = self.getOperatorToken(line, index)
                tokens.append(token)
                token = ''

            elif line[index] in separators:
                if token:
                    tokens.append(token)
                token, index = line[index], index + 1
                tokens.append(token)
                token = ''

            else:
                token += line[index]
                index += 1
        if token:
            tokens.append(token)

        return tokens

    def isIdentifier(self, token):
        return re.match(r'^[a-zA-Z]([a-zA-Z]|[0-9]|_)+$', token) is not None

    def isConstant(self, token):
        return re.match('^(0|[\+\-]?[1-9][0-9]*)$|^\'[a-zA-Z]\'$|^\"[A-Za-z\\s?]+\"$', token) is not None

    def scan(self, fileName):
        file = open(fileName, 'r', encoding='utf-8')
        currentLine = 0
        for line in file:
            currentLine += 1
            for token in self.tokenGenerator(line[0:-1], self.__separators):
                if token in self.__separators + self.__operators + self.__reservedWords:
                    if token != ' ':
                        self.__pif.append((self.__codification[token], -1))
                elif self.isIdentifier(token):
                    id = self.__st.add(token)
                    self.__pif.append((self.__codification['identifier'], id))
                elif self.isConstant(token):
                    id = self.__st.add(token)
                    self.__pif.append((self.__codification['constant'], id))
                else:
                    print('Unknown token ' + token + ' at line ' + str(currentLine))
        self.__write_pif()
        self.__write_st()
        return self.__pif

    def __write_pif(self):
        file = open("PIF.out", "w")
        for elem in self.__pif:
            for cod in self.__codification.keys():
                if self.__codification[cod] == elem[0]:
                    # print(cod, " ", elem[1])
                    file.write(cod + " " + str(elem[1]) + "\n")

    def __write_st(self):
        file = open("ST.out", "w")
        file.write(self.__st.print())

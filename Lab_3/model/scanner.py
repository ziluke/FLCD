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

    def isEscapedQuote(self, line, index):
        return False if index == 0 else line[index - 1] == '\\'

    def isPartOfOperator(self, char):
        for op in self.__operators:
            if char in op:
                return True
        return False

    def getStringToken(self, line, index):
        token = ''
        quoteCount = 0

        while index < len(line) and quoteCount < 2:
            if line[index] == '"' and not self.isEscapedQuote(line, index):
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
        return re.match(r'^[a-zA-Z]([a-zA-Z]|[0-9]|_){,7}$', token) is not None

    def isConstant(self, token):
        return re.match('^(0|[\+\-]?[1-9][0-9]*)$|^\'.\'$|^\".*\"$', token) is not None

    def scan(self, fileName):
        file = open(fileName, 'r', encoding='utf-8')
        currentLine = 0
        for line in file:
            currentLine += 1
            for token in self.tokenGenerator(line[0:-1], self.__separators):
                if token in self.__separators + self.__operators + self.__reservedWords:
                    self.__pif.append((self.__codification[token], -1))
                elif self.isIdentifier(token):
                    id = self.__st.add(token)
                    self.__pif.append((self.__codification['identifier'], id))
                elif self.isConstant(token):
                    id = self.__st.add(token)
                    self.__pif.append((self.__codification['constant'], id))
                else:
                    print('Unknown token ' + token + ' at line ' + str(currentLine))

        return self.__pif

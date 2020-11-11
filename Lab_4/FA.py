class FA:
    def __init__(self, fileName):
        with open(fileName) as file:
            self.Q = self.__token_line(file.readline().strip())
            self.Q0 = self.Q[0]
            self.E = self.__token_line(file.readline().strip())

            self.F = self.__token_line(file.readline().strip())

            for final in self.F:
                if final not in self.Q:
                    raise Exception

            self.Delta = self.__token_transitions(file.readline().strip())

            for key in self.Delta.keys():
                key = key[1:-1]
                key = key.split(",")[0]
                if key not in self.Q:
                    raise Exception

            for value in self.Delta.values():
                for val in value:
                    if val not in self.Q:
                        raise Exception

    def __token_line(self, line):
        token = line.split("=")[1]
        token = token[1:-1]
        token = token.split(",")
        return token

    def __token_transitions(self, line):
        trans_dict = {}
        transitions = line.split("=")[1]
        transitions = transitions[1:-1]
        transitions = transitions.split(";")
        for transition in transitions:
            key, value = transition.split("->")
            # key = key[1:-1]
            # key = key.split(",")
            if key not in trans_dict.keys():
                trans_dict[key] = []
            trans_dict[key].append(value)
        return trans_dict

    def isDeterministic(self):
        for trans in self.Delta.values():
            if len(trans) > 1:
                return False
        return True

    def isAccepted(self, seq):
        state = self.Q0
        while len(seq) > 0:
            elem = seq[0]
            key = "(" + state + "," + elem + ")"
            if key in self.Delta.keys():
                state = self.Delta[key][0]
                seq = seq[1:]
            else:
                return False
        if state in self.F:
            return True

    def __str__(self):
        display = "Displaying FA:\n"
        display = display + "States: " + str(self.Q) + "\n"
        display += "Initial State: " + str(self.Q0) + "\n"
        display = display + "Alphabet: " + str(self.E) + "\n"
        display = display + "Transitions: \n"
        for trans in self.Delta.keys():
            display += str(trans) + "->" + str(self.Delta[trans]) + "\n"
        display += "Final states: " + str(self.F) + "\n"
        display += "Is Deterministic: " + str(self.isDeterministic())
        return display

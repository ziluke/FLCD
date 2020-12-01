package parser;

import grammar.Grammar;

import java.util.*;


public class Parser {
    private final Grammar grammar;
    private HashMap<String, ArrayList<String>> first;
    private HashMap<String, ArrayList<String>> follow;
    private HashMap<Pair, ParsingTableCell> parsingTable;
    Stack<String> inputStack = new Stack<>();
    Stack<String> workingStack = new Stack<>();
    Stack<String> output = new Stack<>();

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.first = new HashMap<>();
        this.first.put("E", new ArrayList<String>() {{
            add("id");
            add("(");
        }});
        this.first.put("E'", new ArrayList<String>() {{
            add("+");
            add("epsilon");
        }});
        this.first.put("T", new ArrayList<String>() {{
            add("id");
            add("(");
        }});
        this.first.put("T'", new ArrayList<String>() {{
            add("*");
            add("epsilon");
        }});
        this.first.put("F", new ArrayList<String>() {{
            add("id");
            add("(");
        }});
//        this.computeFirst();
        this.follow = new HashMap<>();
        this.follow.put("E", new ArrayList<String>() {{
            add("$");
            add(")");
        }});
        this.follow.put("E'", new ArrayList<String>() {{
            add("$");
            add(")");
        }});
        this.follow.put("T", new ArrayList<String>() {{
            add("+");
            add("$");
            add(")");
        }});
        this.follow.put("T'", new ArrayList<String>() {{
            add("+");
            add("$");
            add(")");
        }});
        this.follow.put("F", new ArrayList<String>() {{
            add("*");
            add("+");
            add("$");
            add(")");
        }});
//        this.computeFollow();
        this.parsingTable = new HashMap<>();
        this.computeParsingTable();
    }

    private void computeFirst() {
        ArrayList<HashMap<String, ArrayList<String>>> table = new ArrayList<>();

        //initialization
        HashMap<String, ArrayList<String>> currentColumn = new HashMap<>();
        for (String nonTerminal : this.grammar.getNonTerminals()) {
            ArrayList<ArrayList<String>> productionsForNonTerminal = this.grammar.getProductions().get(nonTerminal);
            ArrayList<String> var = new ArrayList<>();
            for (ArrayList<String> productionForNonTerminal : productionsForNonTerminal)
                if (grammar.getTerminals().contains(productionForNonTerminal.get(0)) || productionForNonTerminal.get(0).equals("epsilon")) {
                    var.add(productionForNonTerminal.get(0));
                }
            currentColumn.put(nonTerminal, this.toSet(var));
        }

        table.add(currentColumn);
        int index = 0;
        //end of initialization

        //F1
        {
            HashMap<String, ArrayList<String>> newColumn = new HashMap<>();


            for (String nonTerminal : this.grammar.getNonTerminals()) {

                ArrayList<ArrayList<String>> productionsForNonTerminal = this.grammar.getProductions().get(nonTerminal);
                ArrayList<String> var = new ArrayList<>();
                for (ArrayList<String> productionForNonTerminal : productionsForNonTerminal) {
                    for (String symbol : productionForNonTerminal)
                        if (this.grammar.getNonTerminals().contains(symbol))
                            var.add(symbol);
                        else
                            break;
                }
                ArrayList<String> toAdd = new ArrayList<>(table.get(0).get(nonTerminal));

                for (int i = 0; i < var.size() - 1; i++) {
                    ArrayList<String> concat = concatenation(table.get(0).get(var.get(i)), table.get(0).get(var.get(i + 1)));
                    toAdd.addAll(concat);
                    toAdd = toSet(toAdd);

                }
                newColumn.put(nonTerminal, toAdd);
            }
            index++;
            table.add(newColumn);

        }
        //end of F1

        //the other columns
        while (!table.get(index).equals(table.get(index - 1))) {
            HashMap<String, ArrayList<String>> newColumn = new HashMap<>();


            for (String nonTerminal : this.grammar.getNonTerminals()) {

                ArrayList<ArrayList<String>> productionsForNonTerminal = this.grammar.getProductions().get(nonTerminal);
                ArrayList<String> var = new ArrayList<>();
                for (ArrayList<String> productionForNonTerminal : productionsForNonTerminal) {
                    for (String symbol : productionForNonTerminal)
                        if (this.grammar.getNonTerminals().contains(symbol))
                            var.add(symbol);
                        else
                            break;
                }
                ArrayList<String> toAdd = new ArrayList<>(table.get(index).get(nonTerminal));
                for (int i = 0; i < var.size() - 1; i++) {
                    ArrayList<String> concat = concatenation(table.get(index).get(var.get(i)), table.get(index).get(var.get(i + 1)));
                    toAdd.addAll(concat);
                    toAdd = toSet(toAdd);

                }
                newColumn.put(nonTerminal, toAdd);
            }
            index++;
            table.add(newColumn);

        }
        this.first = table.get(table.size() - 1);
    }

    private ArrayList<String> toSet(ArrayList<String> var) {
        ArrayList<String> set = new ArrayList<>();
        for (String s : var)
            if (!set.contains(s))
                set.add(s);
        return set;
    }


    private ArrayList<String> concatenation(ArrayList<String> l1, ArrayList<String> l2) {
        ArrayList<String> concatenationList = new ArrayList<>();

        if (l1.isEmpty() || l2.isEmpty())
            return concatenationList;
        for (String s1 : l1) {
            if (!s1.equals("epsilon")) {
                concatenationList.add(s1);
            } else {
                concatenationList.addAll(l2);
            }
        }
        return this.toSet(concatenationList);
    }


    private void computeFollow() {
        ArrayList<HashMap<String, ArrayList<String>>> table = new ArrayList<>();

        //initialization
        HashMap<String, ArrayList<String>> currentColumn = new HashMap<>();
        for (String nonTerminal : this.grammar.getNonTerminals()) {
            ArrayList<String> data = new ArrayList<>();
            if (nonTerminal.equals(grammar.getStartingSymbol())) {
                data.add("epsilon");
            }
            currentColumn.put(nonTerminal, data);
        }

        table.add(currentColumn);
        int index = 0;
        //end of initialization

        //L1
        {
            HashMap<String, ArrayList<String>> newColumn = new HashMap<>();
            //copy from the last column
            for (String nonTerminal : this.grammar.getNonTerminals()) {
                ArrayList<String> toAdd = new ArrayList<>(table.get(index).get(nonTerminal));
                newColumn.put(nonTerminal, toAdd);
            }


            for (String nonTerminal : this.grammar.getNonTerminals()) {

                ArrayList<ArrayList<String>> productionsForNonTerminal = this.grammar.getProductions().get(nonTerminal);

                for (ArrayList<String> productionForNonTerminal : productionsForNonTerminal) {
                    for (int i = 0; i < productionForNonTerminal.size(); i++) {
                        String symbol = productionForNonTerminal.get(i);
                        //if the symbol is a non terminal
                        if (this.grammar.getNonTerminals().contains(symbol)) {
                            //B->aA
                            if (i == productionForNonTerminal.size() - 1) {
                                // FOLLOW(symbol) += FOLLOW(nonTerminal)
                                ArrayList<String> var = new ArrayList<>(table.get(index).get(nonTerminal)); //FOLLOW(nonTerminal)
                                ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                aux.addAll(var);
                                aux = toSet(aux);
                                newColumn.remove(symbol);
                                newColumn.put(symbol, aux);
                            }
                            //B->aAb
                            else {
                                if (this.grammar.getNonTerminals().contains(productionForNonTerminal.get(i + 1))) {
                                    if (this.first.get(productionForNonTerminal.get(i + 1)).contains("epsilon")) {
                                        // FOLLOW(symbol) += FOLLOW(nonTerminal)
                                        ArrayList<String> var = new ArrayList<>(table.get(index).get(nonTerminal)); //FOLLOW(nonTerminal)
                                        ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                        aux.addAll(var);
                                        aux = toSet(aux);
                                        newColumn.remove(symbol);
                                        newColumn.put(symbol, aux);

                                    }
                                    // FOLLOW(symbol) += FIRST(productionForNonTerminal.get(index+1)) \ {epsilon}
                                    ArrayList<String> f = new ArrayList<>(this.first.get(productionForNonTerminal.get(i + 1))); //FIRST(b)
                                    f.remove("epsilon");
                                    ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                    aux.addAll(f);
                                    aux = toSet(aux);
                                    newColumn.remove(symbol);
                                    newColumn.put(symbol, aux);
                                } else {
                                    ArrayList<String> f = new ArrayList<>();
                                    f.add(productionForNonTerminal.get(i + 1));
                                    f.remove("epsilon");
                                    ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                    aux.addAll(f);
                                    aux = toSet(aux);
                                    newColumn.remove(symbol);
                                    newColumn.put(symbol, aux);
                                }
                            }
                        }
                    }
                }

            }
            index++;
            table.add(newColumn);
        }
        //end of L1

        //the other columns
        while (!table.get(index).equals(table.get(index - 1))) {
            HashMap<String, ArrayList<String>> newColumn = new HashMap<>();
            //copy from the last column
            for (String nonTerminal : this.grammar.getNonTerminals()) {
                ArrayList<String> toAdd = new ArrayList<>(table.get(index).get(nonTerminal));
                newColumn.put(nonTerminal, toAdd);
            }


            for (String nonTerminal : this.grammar.getNonTerminals()) {

                ArrayList<ArrayList<String>> productionsForNonTerminal = this.grammar.getProductions().get(nonTerminal);

                for (ArrayList<String> productionForNonTerminal : productionsForNonTerminal) {
                    for (int i = 0; i < productionForNonTerminal.size(); i++) {
                        String symbol = productionForNonTerminal.get(i);
                        //if the symbol is a non terminal
                        if (this.grammar.getNonTerminals().contains(symbol)) {
                            //B->aA
                            if (i == productionForNonTerminal.size() - 1) {
                                // FOLLOW(symbol) += FOLLOW(nonTerminal)
                                ArrayList<String> var = new ArrayList<>(table.get(index).get(nonTerminal)); //FOLLOW(nonTerminal)
                                ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                aux.addAll(var);
                                aux = toSet(aux);
                                newColumn.remove(symbol);
                                newColumn.put(symbol, aux);
                            }
                            //B->aAb
                            else {
                                if (this.grammar.getNonTerminals().contains(productionForNonTerminal.get(i + 1))) {
                                    if (this.first.get(productionForNonTerminal.get(i + 1)).contains("epsilon")) {
                                        // FOLLOW(symbol) += FOLLOW(nonTerminal)
                                        ArrayList<String> var = new ArrayList<>(table.get(index).get(nonTerminal)); //FOLLOW(nonTerminal)
                                        ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                        aux.addAll(var);
                                        aux = toSet(aux);
                                        newColumn.remove(symbol);
                                        newColumn.put(symbol, aux);

                                    }
                                    // FOLLOW(symbol) += FIRST(productionForNonTerminal.get(index+1)) \ {epsilon}
                                    ArrayList<String> f = new ArrayList<>(this.first.get(productionForNonTerminal.get(i + 1))); //FIRST(b)
                                    f.remove("epsilon");
                                    ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                    aux.addAll(f);
                                    aux = toSet(aux);
                                    newColumn.remove(symbol);
                                    newColumn.put(symbol, aux);
                                } else {
                                    ArrayList<String> f = new ArrayList<>();
                                    f.add(productionForNonTerminal.get(i + 1));
                                    f.remove("epsilon");
                                    ArrayList<String> aux = new ArrayList<>(newColumn.get(symbol));
                                    aux.addAll(f);
                                    aux = toSet(aux);
                                    newColumn.remove(symbol);
                                    newColumn.put(symbol, aux);
                                }
                            }
                        }
                    }
                }

            }
            index++;
            table.add(newColumn);

        }
        this.follow = table.get(table.size() - 1);
    }

    private void computeParsingTable() {
        ArrayList<String> allSymbols = (ArrayList<String>) grammar.getNonTerminals().clone();
        allSymbols.addAll(grammar.getTerminals());
        allSymbols.add("$");
        for (String nonTerminal : allSymbols) {
            for (String terminal : grammar.getTerminals()) {
                if (nonTerminal.equals(terminal)) {
                    var pop = new ArrayList<String>();
                    pop.add("pop");
                    parsingTable.put(new Pair(nonTerminal, terminal), new ParsingTableCell(pop, 0));
                } else {
                    parsingTable.put(new Pair(nonTerminal, terminal), null);
                }
            }
            if (nonTerminal.equals("$")) {
                var acc = new ArrayList<String>();
                acc.add("acc");
                parsingTable.put(new Pair(nonTerminal, "$"), new ParsingTableCell(acc, 0));
            } else {
                parsingTable.put(new Pair(nonTerminal, "$"), null);
            }
        }

        var productions = this.grammar.getProductions();

        var prodIndex = 1;

        for (String nonTerminal : grammar.getNonTerminals()) {
            var nonTerminalProds = productions.get(nonTerminal);
            for (ArrayList<String> prod : nonTerminalProds) {
                if (grammar.getTerminals().contains(prod.get(0))) {
                    var rhs = prod.get(0);
                    parsingTable.put(new Pair(nonTerminal, rhs), new ParsingTableCell(prod, prodIndex));
                }
                else if (prod.get(0).equals("epsilon")) {
                    var follow = this.follow.get(nonTerminal);
                    for (String elem : follow) {
                        parsingTable.put(new Pair(nonTerminal, elem), new ParsingTableCell(prod, prodIndex));
                    }
                }
                else if(grammar.getNonTerminals().contains(prod.get(0))){
                    var rhs = this.first.get(prod.get(0));
                    for (String elem : rhs) {
                        parsingTable.put(new Pair(nonTerminal, elem), new ParsingTableCell(prod, prodIndex));
                    }
                }
                prodIndex++;
            }
        }
    }

    public HashMap<String, ArrayList<String>> getFirst() {
        return first;
    }

    public HashMap<String, ArrayList<String>> getFollow() {
        return follow;
    }

    public HashMap<Pair, ParsingTableCell> getParsingTable() {
        return parsingTable;
    }

    public boolean parseSequence(ArrayList<String> sequence){
        initializeStacks(sequence);

        boolean go = true;
        boolean result = true;

        while(go){
            String headOfInputStack = inputStack.peek();
            String headOfWorkingStack = workingStack.peek();

            if(headOfWorkingStack.equals("$") && headOfInputStack.equals("$"))
                return result;

            Pair pair = new Pair(headOfWorkingStack, headOfInputStack);
            ParsingTableCell cell = this.parsingTable.get(pair);
            if(cell == null){
                pair = new Pair(headOfWorkingStack, "epsilon");
                cell = this.parsingTable.get(pair);
                if(cell != null){
                    this.workingStack.pop();
                    continue;
                }
            }

            if(cell == null){
                go = false;
                result = false;
            }
            else{
                ArrayList<String> seq = cell.getSequence();
                int productionNumber = cell.getProductionNumber();

                if (productionNumber == 0 && seq.get(0).equals("acc")) {
                    go = false;
                } else if (productionNumber == 0 && seq.get(0).equals("pop")) {
                    workingStack.pop();
                    inputStack.pop();
                } else {
                    workingStack.pop();
                    if (!seq.get(0).equals("epsilon")) {
                        for(int i = seq.size() - 1; i >= 0; i--)
                            workingStack.push(seq.get(i));

                    }
                    output.push(String.valueOf(productionNumber));
                }
            }



        }

        return result;

    }

    public void  initializeStacks(ArrayList<String> sequence){
        inputStack.clear();
        inputStack.push("$");
        for(int i = sequence.size() - 1; i >= 0; i--)
            inputStack.push(sequence.get(i));

        workingStack.clear();
        workingStack.push("$");
        workingStack.push(grammar.getStartingSymbol());

        output.clear();
        output.push("epsilon");
    }
}
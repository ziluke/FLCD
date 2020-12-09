package table;

import parser.Parser;

import java.util.*;

public class ParsingSequenceTable {
    private List<Node> nodes;
    private HashMap<Integer, HashMap<String, ArrayList<String>>> orderedProductions;
    private List<Integer> outputSequence;
    private Parser parser;

    public ParsingSequenceTable() {
    }

    public ParsingSequenceTable(HashMap<Integer, HashMap<String, ArrayList<String>>> orderedProductions, List<Integer> output, Parser parser) {
        this.orderedProductions = orderedProductions;
        this.outputSequence = output;
        this.parser = parser;
        this._generateParsingSequenceTable();
    }

    private void _generateParsingSequenceTable() {
        int tableindex = 0;
        int outputIndex = 0;

        Node node = new Node();
        node.setId(tableindex);
        node.setFather(-1);
        node.setSibling(-1);
        node.setInfo(parser.getGrammar().getStartingSymbol());

        this.nodes = new ArrayList<>();
        this.nodes.add(node);

        tableindex++;

        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            if (parser.getGrammar().getTerminals().contains(current.getInfo()) || current.getInfo().equals("epsilon")) {
//                this.nodes.add(current);
                stack.pop();
            } else {
                var prod = orderedProductions.get(outputSequence.get(outputIndex));
                var lhs = (String) prod.keySet().toArray()[0];
                var rhs = prod.get(lhs);
                if (current.getInfo().equals(lhs)) {
                    stack.pop();
                    List<Node> aux = new ArrayList<>();
                    tableindex += rhs.size() - 1;
                    for (int i = rhs.size() - 1; i >= 0; i--) {
                        Node newNode = new Node();
                        newNode.setId(tableindex);
                        newNode.setFather(current.getId());
                        tableindex--;

                        if (i > 0) {
                            newNode.setSibling(tableindex);
                        } else {
                            newNode.setSibling(-1);
                        }
                        newNode.setInfo(rhs.get(i));
                        stack.push(newNode);
//                        this.nodes.add(newNode);
                        aux.add(newNode);
                    }
                    Collections.reverse(aux);
                    this.nodes.addAll(aux);
                    tableindex += rhs.size() + 1;
                    outputIndex++;
                }
            }
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }
}

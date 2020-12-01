import grammar.Grammar;
import parser.Pair;
import parser.Parser;
import parser.ParsingTableCell;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("grammar.in");
        Parser parser = new Parser(grammar);

        System.out.println("Seq: " + parser.parseSequence(new ArrayList<>() {{
            add("id");
            add("*");
            add("(");
            add("id");
            add("+");
            add("id");
            add(")");
        }}));

//        System.out.println("Seq: " + parser.parseSequence(new ArrayList<>() {{
//            add("(");
//            add(")");
//        }}));

        var builder = new StringBuilder(" ");

        var terminals = grammar.getTerminals();
        terminals.add("$");

        for (var elem : terminals
        ) {
            builder.append(elem).append(",");
        }
        builder.append("\n");


        for (var nonTerminal : grammar.getNonTerminals()) {
            builder.append(nonTerminal);
            for (var terminal : terminals) {
                Pair pair = new Pair(nonTerminal, terminal);
                builder.append(",").append(parser.getParsingTable().get(pair)).append(" ");
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }
}

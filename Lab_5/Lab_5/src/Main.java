import grammar.Grammar;
import parse_sequence_table.ParsingSequenceTable;
import parser.Pair;
import parser.Parser;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("grammar.in");

        Parser parser = new Parser(grammar);
        try {
            parser.computeParsingTable();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        var seq = parser.parseSequence(new ArrayList<>() {{
            add("id");
            add("*");
            add("(");
            add("id");
            add("+");
            add("id");
            add(")");
        }});

//        var seq = parser.parseSequence(new ArrayList<>() {{
//            add("(");
//            add(")");
//        }});

        System.out.println("Seq: " + seq);

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

        ParsingSequenceTable table = new ParsingSequenceTable(grammar.getProductionWithOrderNumber(), seq, parser);

        var nodes = table.getNodes();

        nodes.forEach(System.out::println);

    }


}

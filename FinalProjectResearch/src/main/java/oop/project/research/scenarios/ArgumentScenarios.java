package oop.project.research.scenarios;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.util.Map;

public final class ArgumentScenarios {

    public static Map<String, Object> add(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("add").build();
        parser.addArgument("left").type(int.class);
        parser.addArgument("right").type(int.class);
        var namespace = parser.parseArgs(arguments);
        //Note: namespace.getAttrs() returns a Map directly, but a major
        //part of this problem is to ensure we can safely get arguments with
        //the correct static type for use in a real program!
        var left = namespace.getInt("left"); //uses getInt to return int
        int right = namespace.get("right"); //uses type inference - clever but risky!
        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> sub(String[] arguments) throws ArgumentParserException {
        throw new UnsupportedOperationException("TODO");
    }

    public static Map<String, Object> fizzbuzz(String[] arguments) throws ArgumentParserException {
        throw new UnsupportedOperationException("TODO");
    }

    public static Map<String, Object> difficulty(String[] arguments) throws ArgumentParserException {
        throw new UnsupportedOperationException("TODO");
    }

    public static Map<String, Object> date(String[] arguments) throws ArgumentParserException {
        throw new UnsupportedOperationException("TODO");
    }

}

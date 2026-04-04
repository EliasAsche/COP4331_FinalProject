package oop.project.research.scenarios;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("mul").build();
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

    public static Map<String, Object> div(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("div").build();
        parser.addArgument("--left").type(Double.class).required(true);
        parser.addArgument("--right").type(Double.class).required(true);
        var namespace = parser.parseArgs(arguments);
        var left = namespace.getDouble("left");
        var right = namespace.getDouble("right");
        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> echo(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("echo").build();
        parser.addArgument("message")
            .nargs("?")
            .setDefault("echo,echo,echo...");
        var namespace = parser.parseArgs(arguments);
        var message = namespace.getString("message");
        return Map.of("message", message);
    }

    public static Map<String, Object> search(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("search").build();
        parser.addArgument("term");
        parser.addArgument("--case-insensitive", "-i")
            .choices("true","false")
            .setDefault(false)
            .setConst(true)
            .nargs("?");
        
        var namespace = parser.parseArgs(arguments);
        var term = namespace.getString("term");
        var raw = namespace.get("case_insensitive");
        Boolean caseInsensitive;
        if (raw instanceof String){
            caseInsensitive = Boolean.parseBoolean((String) raw);
        }else {
            caseInsensitive = (Boolean) raw;
        }
        return Map.of("term", term, "case-insensitive", caseInsensitive);
    }

    public static Map<String, Object> dispatch(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("dispatch").build();
        var subparsers = parser.addSubparsers().dest("type");
        var staticParser = subparsers.addParser("static");
        staticParser.addArgument("value").type(Integer.class);
        var dynamicParser = subparsers.addParser("dynamic");
        dynamicParser.addArgument("value");
        var namespace = parser.parseArgs(arguments);
        var type = namespace.getString("type");
        var value = namespace.get("value");
        return Map.of("type", type, "value", value);
    }

}

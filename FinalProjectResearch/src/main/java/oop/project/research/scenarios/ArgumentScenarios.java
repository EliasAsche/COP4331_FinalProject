package oop.project.research.scenarios;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.impl.Arguments;
import java.time.LocalDate;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentType;
import java.time.format.DateTimeParseException;

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
        var parser = ArgumentParsers.newFor("sub").build();
        parser.addArgument("left").type(double.class);
        parser.addArgument("right").type(double.class);
        var namespace = parser.parseArgs(arguments);
        var left = namespace.getDouble("left");
        double right = namespace.get("right");
        return Map.of("left", left, "right", right);
    }

    public static Map<String, Object> fizzbuzz(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("fizzbuzz").build();
        parser.addArgument("number").type(int.class).choices(Arguments.range(1, 101));
        var namespace = parser.parseArgs(arguments);
        var number = namespace.getInt("number");
        return Map.of("number", number);

    }

    public static Map<String, Object> difficulty(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("difficulty").build();
        parser.addArgument("difficulty").choices("peaceful", "easy", "normal", "hard");
        var namespace = parser.parseArgs(arguments);
        var difficulty = namespace.getString("difficulty");
        return Map.of("difficulty", difficulty);
    }

    public static Map<String, Object> date(String[] arguments) throws ArgumentParserException {
        var parser = ArgumentParsers.newFor("date").build();
        parser.addArgument("date").type(new ArgumentType<LocalDate>() {
            @Override
            public LocalDate convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
                try {
                    return LocalDate.parse(value);
                } catch (DateTimeParseException e) {
                    throw new ArgumentParserException(e.getMessage(), parser);
                }
            }
        });
        var namespace = parser.parseArgs(arguments);
        LocalDate date = namespace.get("date");
        return Map.of("date", date);
    }

}

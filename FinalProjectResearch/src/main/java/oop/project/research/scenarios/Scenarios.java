package oop.project.research.scenarios;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.util.Map;

public final class Scenarios {

    public static Map<String, Object> parse(String command) throws ArgumentParserException {
        // Recall argparse4j requires a String[] arguments. Space-splitting the
        // input is sufficient but doesn't support quoted arguments.
        var split = command.split(" ", 2);
        var arguments = split.length == 2 ? split[1].split(" ") : new String[] {};
        return switch (split[0]) {
            case "add" -> ArgumentScenarios.add(arguments);
            case "sub" -> ArgumentScenarios.sub(arguments);
            case "fizzbuzz" -> ArgumentScenarios.fizzbuzz(arguments);
            case "difficulty" -> ArgumentScenarios.difficulty(arguments);
            case "date" -> ArgumentScenarios.date(arguments);
            case "mul" -> CommandScenarios.mul(arguments);
            case "div" -> CommandScenarios.div(arguments);
            case "echo" -> CommandScenarios.echo(arguments);
            case "search" -> CommandScenarios.search(arguments);
            case "dispatch" -> CommandScenarios.dispatch(arguments);
            default -> throw new AssertionError();
        };
    }

}

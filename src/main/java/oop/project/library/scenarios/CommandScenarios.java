package oop.project.library.scenarios;

import oop.project.library.argument.Argument;
import oop.project.library.argument.Arguments;
import oop.project.library.command.Command;

import java.util.List;
import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        try {
            var left = Argument.of("left", Arguments.INTEGER);
            var right = Argument.of("right", Arguments.INTEGER);
            var command = Command.of("mul").positional(left).positional(right);
            var result = command.parse(arguments);
            return Map.of("left", result.get(left), "right", result.get(right));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed mul", e);
        }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            var left = Argument.of("left", Arguments.DOUBLE);
            var right = Argument.of("right", Arguments.DOUBLE);
            var command = Command.of("div").named(left).named(right);
            var result = command.parse(arguments);
            return Map.of("left", result.get(left), "right", result.get(right));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed div", e);
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            var message = Argument.of("message", Arguments.STRING);
            var command = Command.of("echo").positional(message, "echo,echo,echo...");
            var result = command.parse(arguments);
            return Map.of("message", result.get(message));
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid echo.", e);
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        try {
            var term = Argument.of("term", Arguments.STRING);
            var caseInsensitive = Argument.of("case-insensitive", Arguments.BOOLEAN);
            var command = Command.of("search")
                .positional(term)
                .named(caseInsensitive, List.of("case-insensitive", "i"), false, true);
            var result = command.parse(arguments);
            return Map.of("term", result.get(term), "case-insensitive", result.get(caseInsensitive));
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid search.", e);
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            var staticValue = Argument.of("value", Arguments.INTEGER);
            var dynamicValue = Argument.of("value", Arguments.STRING);
            var command = Command.of("dispatch")
                .subcommand(Command.of("static").positional(staticValue))
                .subcommand(Command.of("dynamic").positional(dynamicValue));
            var result = command.parse(arguments);
            var type = result.subcommand().get();
            if (type.equals("static")) {
                return Map.of("type", type, "value", result.get(staticValue));
            } else {
                return Map.of("type", type, "value", result.get(dynamicValue));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid dispatch.", e);
        }
    }

}
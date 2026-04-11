package oop.project.library.scenarios;

import oop.project.library.argument.Argument;
import oop.project.library.argument.Arguments;
import oop.project.library.command.Command;

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
        throw new UnsupportedOperationException("TODO (MVP)");
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (MVP)");
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (MVP)");
    }

}

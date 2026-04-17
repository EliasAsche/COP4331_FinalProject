package oop.project.library.scenarios;

import oop.project.library.argument.Argument;
import oop.project.library.argument.Arguments;
import oop.project.library.input.Input;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class ArgumentScenarios {

    private enum Difficulty {
        PEACEFUL, EASY, NORMAL, HARD
    }

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            var values = positional(arguments, 2);
            var left = Argument.of("left", Arguments.INTEGER).parse(values.get(0));
            var right = Argument.of("right", Arguments.INTEGER).parse(values.get(1));
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add.", e);
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        try {
            var values = positional(arguments, 2);
            var left = Argument.of("left", Arguments.DOUBLE).parse(values.get(0));
            var right = Argument.of("right", Arguments.DOUBLE).parse(values.get(1));
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid sub.", e);
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            var values = positional(arguments, 1);
            var number = Argument.of("number", Arguments.INTEGER)
                    .validate(Arguments.range(1, 100), "Expected value in range [1, 100].")
                    .parse(values.get(0));
            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid fizzbuzz.", e);
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            var values = positional(arguments, 1);
            var difficulty = Argument.of("difficulty", Arguments.enumeration(Difficulty.class)).parse(values.get(0));
            return Map.of("difficulty", difficulty.name().toLowerCase());
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty.", e);
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            var values = positional(arguments, 1);
            var date = Argument.of("date", Arguments.custom(LocalDate::parse)).parse(values.get(0));
            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid date.", e);
        }
    }

    private static List<String> positional(String arguments, int expected) {
        var basicArgs = new Input(arguments).parseBasicArgs();
        if (!basicArgs.named().isEmpty()) {
            throw new RuntimeException("Unexpected named arguments.");
        }
        if (basicArgs.positional().size() != expected) {
            throw new RuntimeException("Expected " + expected + " positional arguments.");
        }
        return basicArgs.positional();
    }

}
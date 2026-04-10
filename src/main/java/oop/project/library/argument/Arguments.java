package oop.project.library.argument;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Arguments {

    public static final ArgumentType<Boolean> BOOLEAN = value -> {
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        throw new RuntimeException("Expected true or false.");
    };

    public static final ArgumentType<Integer> INTEGER = Integer::parseInt;
    public static final ArgumentType<Double> DOUBLE = Double::parseDouble;
    public static final ArgumentType<String> STRING = value -> value;

    public static <T> ArgumentType<T> custom(Function<String, T> parser) {
        return parser::apply;
    }

    public static <T extends Comparable<T>> Predicate<T> range(T min, T max) {
        return value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static Predicate<String> choices(String... values) {
        var choices = Set.of(values);
        return choices::contains;
    }

    private Arguments() {}

}

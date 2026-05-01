package oop.project.library.argument;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Arguments {

    public static final ArgumentType<Boolean> BOOLEAN = value -> {
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        throw new ArgumentException("Expected true or false.");
    };

    public static final ArgumentType<Integer> INTEGER = value -> {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentException("Expected integer.", e);
        }
    };

    public static final ArgumentType<Double> DOUBLE = value -> {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ArgumentException("Expected double.", e);
        }
    };

    public static final ArgumentType<String> STRING = value -> value;

    public static <T> ArgumentType<T> custom(Function<String, T> parser) {
        return parser::apply;
    }

    public static <E extends Enum<E>> ArgumentType<E> enumeration(Class<E> enumType) {
        return value -> {
            for (var constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(value)) {
                    return constant;
                }
            }
            throw new ArgumentException(
                    "Expected one of " + Arrays.toString(enumType.getEnumConstants()) + "."
            );
        };
    }

    /**
     * Creates a reusable inclusive range validation predicate for any comparable type.
     * This is used for both integer and decimal range checks because Integer, Double,
     * and similar numeric wrapper types implement {@link Comparable}.
     *
     * @param min the minimum allowed value, inclusive
     * @param max the maximum allowed value, inclusive
     * @param <T> the comparable value type being checked
     * @return a predicate that returns true when the value is within {@code [min, max]}
     */
    public static <T extends Comparable<T>> Predicate<T> range(T min, T max) {
        return value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static Predicate<String> choices(String... values) {
        return value -> {
            for (var choice : values) {
                if (choice.equals(value)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<String> regex(String pattern) {
        var compiled = Pattern.compile(pattern);
        return value -> compiled.matcher(value).matches();
    }

    private Arguments() {}

}
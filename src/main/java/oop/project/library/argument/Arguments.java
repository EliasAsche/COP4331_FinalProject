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
        return value -> {
            try {
                return parser.apply(value);
            } catch (RuntimeException e) {
                throw new ArgumentException("Invalid custom value.", e);
            }
        };
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
     * Creates a validation predicate that checks whether a comparable value is
     * within the inclusive range [min, max].
     *
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @param <T> a comparable type, such as Integer or Double
     * @return a predicate that returns true when the value is within the range
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
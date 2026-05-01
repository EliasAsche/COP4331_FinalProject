package oop.project.library.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Argument<T> {

    private record Validation<T>(Predicate<T> predicate, String message) {}

    private final String name;
    private final ArgumentType<T> type;
    private final List<Validation<T>> validations;

    private Argument(String name, ArgumentType<T> type) {
        this.name = name;
        this.type = type;
        this.validations = new ArrayList<>();
    }

    public static <T> Argument<T> of(String name, ArgumentType<T> type) {
        return new Argument<>(name, type);
    }

    public Argument<T> validate(Predicate<T> predicate, String message) {
        validations.add(new Validation<>(predicate, message));
        return this;
    }

    public String name() {
        return name;
    }

    /**
     * Parses the provided String using this argument's type and then applies any
     * validation rules attached to the argument.
     *
     * @param value the raw input String
     * @return the parsed and validated value
     * @throws ArgumentException if parsing fails or any validation rule is not satisfied
     */

    public T parse(String value) {
        final T parsed;

        try {
            parsed = type.parse(value);
        } catch (ArgumentException e) {
            throw new ArgumentException("Invalid " + name + ": " + value, e);
        } catch (RuntimeException e) {
            throw new ArgumentException("Invalid " + name + ": " + value, e);
        }

        for (var validation : validations) {
            if (!validation.predicate().test(parsed)) {
                throw new ArgumentException("Invalid " + name + ": " + validation.message());
            }
        }

        return parsed;
    }

}
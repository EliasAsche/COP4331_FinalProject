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

    public T parse(String value) {
        final T parsed;
        try {
            parsed = type.parse(value);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid " + name + ": " + value, e);
        }

        for (var validation : validations) {
            if (!validation.predicate().test(parsed)) {
                throw new RuntimeException("Invalid " + name + ": " + validation.message());
            }
        }

        return parsed;
    }

}

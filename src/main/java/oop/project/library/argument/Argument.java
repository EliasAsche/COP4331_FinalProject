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
     * Parses the provided String using this argument's configured {@link ArgumentType},
     * then applies each validation rule attached with {@link #validate(Predicate, String)}.
     * Built-in argument types and custom argument types are both handled through the same
     * {@code ArgumentType<T>} abstraction, so custom parsing support does not require a
     * separate argument subclass.
     *
     * @param value the raw input String to parse
     * @return the parsed and validated value
     * @throws ArgumentException if the value cannot be parsed or fails validation
     */
    public T parse(String value) {
        var parsed = type.parse(value);

        for (var validation : validations) {
            if (!validation.predicate().test(parsed)) {
                throw new ArgumentException("Invalid " + name + ": " + validation.message());
            }
        }

        return parsed;
    }

}
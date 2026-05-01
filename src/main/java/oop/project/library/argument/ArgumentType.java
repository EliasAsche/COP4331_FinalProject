package oop.project.library.argument;

@FunctionalInterface
public interface ArgumentType<T> {
    /**
     * Parses a raw String input into a value of type T.
     *
     * @param value the raw input String
     * @return the parsed value
     * @throws ArgumentException if the value cannot be parsed as this argument type
     */
    T parse(String value);

}
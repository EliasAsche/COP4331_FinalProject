package oop.project.library.command;

import oop.project.library.argument.Argument;

import java.util.Map;
import java.util.Optional;

public final class ParsedArgs {

    private final Map<String, Object> values;
    private final Optional<String> subcommand;

    ParsedArgs(Map<String, Object> values, Optional<String> subcommand) {
        this.values = values;
        this.subcommand = subcommand;
    }

    /**
     * Retrieves the parsed value for an argument, returned as the argument's
     * declared type. The {@link Argument} reference itself acts as a typed key,
     * so callers do not cast at the call site
     * (e.g. {@code int left = result.get(leftArg);}). The internal cast is
     * unchecked but sound: values are inserted by {@link Command#parse(String)}
     * only after passing through this argument's typed parser.
     *
     * @param argument the same {@link Argument} reference that was registered on the command
     * @param <T> the value type carried by {@code argument}
     * @return the parsed value, typed as {@code T}
     * @throws CommandException if no value was parsed for this argument
     *         (typically because it belongs to a different subcommand branch
     *         than the one the user actually invoked)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Argument<T> argument) {
        if (!values.containsKey(argument.name())) {
            throw new CommandException("No value parsed for argument ");
        }
        return (T) values.get(argument.name());
    }

    public Optional<String> subcommand() {
        return subcommand;
    }

}

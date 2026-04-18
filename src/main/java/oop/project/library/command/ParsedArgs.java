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

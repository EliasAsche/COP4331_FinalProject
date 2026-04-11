package oop.project.library.command;

import oop.project.library.argument.Argument;

import java.util.Map;

public final class ParsedArgs {

    private final Map<String, Object> values;

    ParsedArgs(Map<String, Object> values) {
        this.values = values;
    }

    public <T> T get(Argument<T> argument) {
        return (T) values.get(argument.name());
    }

}

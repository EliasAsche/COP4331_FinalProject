package oop.project.library.command;

import oop.project.library.argument.Argument;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Command {

    private final String name;
    private final List<Argument<?>> positionalArgs;
    private final List<Argument<?>> namedArgs;

    private Command(String name) {
        this.name = name;
        this.positionalArgs = new ArrayList<>();
        this.namedArgs = new ArrayList<>();
    }

    public static Command of(String name) {
        return new Command(name);
    }

    public <T> Command positional(Argument<T> argument) {
        positionalArgs.add(argument);
        return this;
    }

    public <T> Command named(Argument<T> argument) {
        namedArgs.add(argument);
        return this;
    }

    public ParsedArgs parse(String input) {
        var basicArgs = new Input(input).parseBasicArgs();
        var values = new HashMap<String, Object>();

        if (basicArgs.positional().size() != positionalArgs.size()) {
            throw new RuntimeException("Expected " + positionalArgs.size());
        }

        for (int i = 0; i < positionalArgs.size(); i++) {
            var argument = positionalArgs.get(i);
            values.put(argument.name(), parseArgument(argument, basicArgs.positional().get(i)));
        }

        for (var argument : namedArgs) {
            var raw = basicArgs.named().get(argument.name());
            if (raw == null) {
                throw new RuntimeException("missing args" + argument.name() + ".");
            }
            values.put(argument.name(), parseArgument(argument, raw));
        }


        return new ParsedArgs(values);
    }

    private static <T> T parseArgument(Argument<T> argument, String value) {
        return argument.parse(value);
    }

}

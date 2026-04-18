package oop.project.library.command;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentException;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Command {

    private sealed interface ArgMetadata {

        record PositionalMetadata<T>(
            Argument<T> argument,
            Optional<Object> defaultValue
        ) implements ArgMetadata {}

        record NamedMetadata<T>(
            Argument<T> argument,
            List<String> names,
            Optional<Object> defaultValue,
            Optional<Object> presentDefaultValue
        ) implements ArgMetadata {}

        record SubcommandMetadata(
            Map<String, Command> subcommands
        ) implements ArgMetadata {}

    }

    private final String name;
    private final List<ArgMetadata.PositionalMetadata<?>> positionals = new ArrayList<>();
    private final List<ArgMetadata.NamedMetadata<?>> named_args = new ArrayList<>();
    private ArgMetadata.SubcommandMetadata subcommands = null;

    private Command(String name) {
        this.name = name;
    }

    public static Command of(String name) {
        return new Command(name);
    }

    public <T> Command positional(Argument<T> argument) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add pos args");
        }
        checkDuplicate(argument.name());
        positionals.add(new ArgMetadata.PositionalMetadata<>(argument, Optional.empty()));
        return this;
    }

    public <T> Command positional(Argument<T> argument, T defaultValue) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add pos args");
        }
        checkDuplicate(argument.name());
        positionals.add(new ArgMetadata.PositionalMetadata<>(argument, Optional.of(defaultValue)));
        return this;
    }

    public <T> Command named(Argument<T> argument) {
        return named(argument, List.of(argument.name()));
    }

    public <T> Command named(Argument<T> argument, List<String> names) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add named args");
        }
        if (names.isEmpty()) {
            throw new CommandException(" must have at least one name.");
        }
        for (var n : names) {
            checkDuplicate(n);
        }
        named_args.add(new ArgMetadata.NamedMetadata<>(argument, new ArrayList<>(names), Optional.empty(), Optional.empty()));
        return this;
    }

    public <T> Command named(Argument<T> argument, List<String> names, T defaultValue, T presentDefaultValue) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add named args");
        }
        if (names.isEmpty()) {
            throw new CommandException(" must have at least one name.");
        }
        for (var n : names) {
            checkDuplicate(n);
        }
        named_args.add(new ArgMetadata.NamedMetadata<>(
            argument,
            new ArrayList<>(names),
            Optional.of(defaultValue),
            Optional.of(presentDefaultValue)
        ));
        return this;
    }

    public Command subcommand(Command child) {
        if (!positionals.isEmpty() || !named_args.isEmpty()) {
            throw new CommandException(" cannot mix subcommands ");
        }
        if (subcommands == null) {
            subcommands = new ArgMetadata.SubcommandMetadata(new LinkedHashMap<>());
        }
        if (subcommands.subcommands().containsKey(child.name)) {
            throw new CommandException("Duplicate subcommand name ");
        }
        subcommands.subcommands().put(child.name, child);
        return this;
    }

    public ParsedArgs parse(String input) {
        var basicArgs = new Input(input).parseBasicArgs();

        if (subcommands != null) {
            if (basicArgs.positional().isEmpty()) {
                throw new ArgumentException("Command requires a subcommand: ");
            }
            var subName = basicArgs.positional().get(0);
            var child = subcommands.subcommands().get(subName);
            if (child == null) {
                throw new ArgumentException("Unknown subcommand ");
            }
            var remaining = new BasicArgs(
                basicArgs.positional().subList(1, basicArgs.positional().size()),
                basicArgs.named()
            );
            return child.parseArgs(remaining, Optional.of(subName));
        }

        return parseArgs(basicArgs, Optional.empty());
    }

    private ParsedArgs parseArgs(BasicArgs basicArgs, Optional<String> subcommandName) {
        var values = new HashMap<String, Object>();

        var provided = basicArgs.positional().size();
        var max = positionals.size();
        var minRequired = 0;
        for (var p : positionals) {
            if (p.defaultValue().isEmpty()) minRequired++;
        }
        if (provided < minRequired) {
            throw new ArgumentException("Command expected at least 1 pos arg");
        }
        if (provided > max) {
            throw new ArgumentException("Command expected 1 pos arg ");
        }
        for (int i = 0; i < positionals.size(); i++) {
            var slot = positionals.get(i);
            if (i < provided) {
                values.put(slot.argument().name(), slot.argument().parse(basicArgs.positional().get(i)));
            } else {
                values.put(slot.argument().name(), slot.defaultValue().orElseThrow());
            }
        }

        var seenNamed = new HashMap<String, String>();
        for (var entry : basicArgs.named().entrySet()) {
            ArgMetadata.NamedMetadata<?> slot = null;
            for (var n : named_args) {
                if (n.names().contains(entry.getKey())) {
                    slot = n;
                    break;
                }
            }
            if (slot == null) {
                throw new ArgumentException("Unexpected named argument");
            }
            if (seenNamed.containsKey(slot.argument().name())) {
                throw new ArgumentException(" more than 1 named arg");
            }
            seenNamed.put(slot.argument().name(), entry.getValue());
        }

        for (var slot : named_args) {
            var raw = seenNamed.get(slot.argument().name());
            if (raw == null) {
                if (slot.defaultValue().isPresent()) {
                    values.put(slot.argument().name(), slot.defaultValue().get());
                } else {
                    throw new ArgumentException("Missing named arg.");
                }
            } else if (raw.isEmpty()) {
                if (slot.presentDefaultValue().isPresent()) {
                    values.put(slot.argument().name(), slot.presentDefaultValue().get());
                } else {
                    throw new ArgumentException("Named argument need val");
                }
            } else {
                values.put(slot.argument().name(), slot.argument().parse(raw));
            }
        }

        return new ParsedArgs(values, subcommandName);
    }

    private void checkDuplicate(String candidate) {
        for (var p : positionals) {
            if (p.argument().name().equals(candidate)) {
                throw new CommandException("Duplicate argument name");
            }
        }
        for (var n : named_args) {
            if (n.names().contains(candidate)) {
                throw new CommandException("Duplicate argument name");
            }
        }
    }

}

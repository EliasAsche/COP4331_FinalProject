package oop.project.library.command;

import oop.project.library.argument.Argument;
import oop.project.library.argument.ArgumentException;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Command {

    private record PositionalMetadata<T>(
        Argument<T> argument,
        Optional<Object> defaultValue
    ) {}

    private record NamedMetadata<T>(
        Argument<T> argument,
        Optional<Object> defaultValue,
        Optional<Object> presentDefaultValue
    ) {}

    private final String name;
    private final List<PositionalMetadata<?>> positionals = new ArrayList<>();
    private final Set<NamedMetadata<?>> namedSlots = new LinkedHashSet<>();
    private final Map<String, NamedMetadata<?>> namedByAlias = new LinkedHashMap<>();
    private Map<String, Command> subcommands = null;

    private Command(String name) {
        this.name = name;
    }

    public static Command of(String name) {
        return new Command(name);
    }

    /**
     * Registers the required positional arguments for a command.
     * Positionals are consumed in the order they were added.
     *
     * @param argument the typed argument to register
     * @param <T> the parsed value type carried
     * @return this command
     * @throws CommandException A command level exception if subcommands are already registered or if the
     *         argument name collides with an existing positional
     */
    public <T> Command positional(Argument<T> argument) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add pos args");
        }
        checkDuplicate(argument.name());
        positionals.add(new PositionalMetadata<>(argument, Optional.empty()));
        return this;
    }

    /**
     * Registers an optional positional argument with a default value, used if not provided.
     * Defaults follow required positionals in registration order.
     * Supplying fewer values than required positionals fails at parse time.
     *
     * @param argument the typed argument descriptor to register
     * @param defaultValue the value substituted if caller omits this positional
     * @param <T> the parsed value type carried
     * @return this command
     * @throws CommandException A command level exception if subcommands are already registered or if the
     *         argument name collides with an existing positional
     */
    public <T> Command positional(Argument<T> argument, T defaultValue) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add pos args");
        }
        checkDuplicate(argument.name());
        positionals.add(new PositionalMetadata<>(argument, Optional.of(defaultValue)));
        return this;
    }

    /**
     * Registers a required named argument using the name of the argument as a single flag alias.
     *
     * @param argument the typed argument descriptor to register
     * @param <T> the parsed value type carried
     * @return this command
     * @throws CommandException if subcommands are registered or the name collides
     */
    public <T> Command named(Argument<T> argument) {
        return named(argument, List.of(argument.name()));
    }

    /**
     * Registers a required named argument supplied under any of the given alias names.
     * Each alias stores its own lookup pointing at the underlying metadata.
     *
     * @param argument the typed argument descriptor
     * @param names the flag names that trigger this argument
     * @param <T> the parsed value type carried
     * @return this command
     * @throws CommandException if subcommands already exist, names is empty, or a name collides
     */
    public <T> Command named(Argument<T> argument, List<String> names) {
        return registerNamed(argument, names, Optional.empty(), Optional.empty());
    }

    /**
     * Registers a named arg with default behavior.
     * When the flag is missing, {@code defaultValue} is used.
     * When the flag appears with no following value ({@code -i}), {@code presentDefaultValue} is used.
     * When the flag has a value ({@code --i true}), the value is parsed normally.
     *
     * @param argument the typed argument descriptor
     * @param names the flag names that may trigger this argument
     * @param defaultValue value used when the flag is absent from input
     * @param presentDefaultValue value used when the flag is present but has no provided value
     * @param <T> the parsed value type carried
     * @return this command
     * @throws CommandException if subcommands exist, names is empty, or a name collides
     */
    public <T> Command named(Argument<T> argument, List<String> names, T defaultValue, T presentDefaultValue) {
        return registerNamed(argument, names, Optional.of(defaultValue), Optional.of(presentDefaultValue));
    }

    private <T> Command registerNamed(
        Argument<T> argument,
        List<String> names,
        Optional<Object> defaultValue,
        Optional<Object> presentDefaultValue
    ) {
        if (subcommands != null) {
            throw new CommandException("Command cannot add named args");
        }
        if (names.isEmpty()) {
            throw new CommandException(" must have at least one name.");
        }
        for (var n : names) {
            checkDuplicate(n);
        }
        var slot = new NamedMetadata<>(argument, defaultValue, presentDefaultValue);
        namedSlots.add(slot);
        for (var n : names) {
            namedByAlias.put(n, slot);
        }
        return this;
    }

    public Command subcommand(Command child) {
        if (!positionals.isEmpty() || !namedSlots.isEmpty()) {
            throw new CommandException(" cannot mix subcommands ");
        }
        if (subcommands == null) {
            subcommands = new LinkedHashMap<>();
        }
        if (subcommands.containsKey(child.name)) {
            throw new CommandException("Duplicate subcommand name ");
        }
        subcommands.put(child.name, child);
        return this;
    }

    /**
     * Tokenizes the input to a typed {@link ParsedArgs}.
     * If the command has subcommands, the first position token is treated as the subcommand
     * and parsing is delegated to the matching child command provided. If not, positionals
     * are matched in order and the named arguments are resolved using an alias map,
     * applying defaults, present defaults, or detecting unknown flags.
     *
     * @param input the raw command input after base command
     * @return a {@link ParsedArgs} keyed by the original {@link Argument} references.
     *         supports type-safe extraction without casts at the call site
     * @throws ArgumentException if the input is missing required arguments, has
     *         too many positionals, references an unknown subcommand, uses an
     *         unknown named flag, or fails to parse/validate any individual value
     */
    public ParsedArgs parse(String input) {
        var basicArgs = new Input(input).parseBasicArgs();

        if (subcommands != null) {
            if (basicArgs.positional().isEmpty()) {
                throw new ArgumentException("Command requires a subcommand: ");
            }
            var subName = basicArgs.positional().get(0);
            var child = subcommands.get(subName);
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
            var slot = namedByAlias.get(entry.getKey());
            if (slot == null) {
                throw new ArgumentException("Unexpected named argument");
            }
            if (seenNamed.containsKey(slot.argument().name())) {
                throw new ArgumentException(" more than 1 named arg");
            }
            seenNamed.put(slot.argument().name(), entry.getValue());
        }

        for (var slot : namedSlots) {
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
        if (namedByAlias.containsKey(candidate)) {
            throw new CommandException("Duplicate argument name");
        }
    }

}

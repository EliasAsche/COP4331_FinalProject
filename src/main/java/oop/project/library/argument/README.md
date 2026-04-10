# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

Used a small generic `Argument<T>` wrapper so parsing and validation stay together instead of being split across the scenarios.

Used `ArgumentType<T>` as the core abstraction so the built-in types and custom types use the same overall flow.

Kept range and choice validation separate from the actual primitive parsing so it is easier to extend later for stuff like enums or regex checks.

Right now the scenarios still return `Map<String, Object>` because that is what the provided tests expect, but the actual typed parsing happens before that point.

## PoC Design Analysis

### Individual Review (Argument Lead)

One thing I like is that the argument API is pretty small. The `Argument<T>` plus `ArgumentType<T>` setup was enough to handle ints, doubles, strings, and a custom `LocalDate` case without needing a bunch of separate classes.

Another good choice was keeping validation chained onto the argument itself. That made stuff like fizzbuzz range checks and difficulty choices feel pretty direct.

One weaker part is that the error handling is still pretty generic right now. It works for the PoC, but the messages could definitely be more specific.

Another less-good part is that the scenarios still have to do positional count checks themselves. It works, but ideally more of that would probably live in the command system instead.

### Individual Review (Command Lead)

The provided Input structure seems useful since it already separates positional and named arguments in a simple way.

One possible issue is that the command side will probably get more awkward once defaults and subcommands get added, so the current PoC structure may need to grow pretty quickly.

### Team Review

One thing we are still not fully sure about is how much validation responsibility should stay in the argument system versus the command system.

We also still need to think more about how the current structure should grow once named defaults and subcommands become part of the MVP.

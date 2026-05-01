# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

For the argument system, the main goal was keeping it polymorphic without making the API feel too heavy. That is why the main abstraction is `ArgumentType<T>`. It let us support the normal primitive cases, custom parsing, enums, and regex-related validation without hardcoding a bunch of one-off argument classes.

Another design choice was keeping validation attached directly to `Argument<T>` through `validate(...)`. That felt better than scattering checks around the scenarios since ranges, choices, and regex validation can stay close to the actual value being parsed.

We also added `ArgumentException` as the standardized error type for the argument side. The point there was mainly to stop the argument system from just throwing random generic errors and make failures easier to reason about.

One thing that still feels a little rough is that the command system still influences how clean the argument side looks in the scenarios. The argument system itself is mostly focused on String -> parsed value, but some responsibility still ends up looking a little mixed once the scenarios get involved.

## Individual Review

### Good design decisions

One good design decision is using `ArgumentType<T>` as the main abstraction. That made it easy to support primitive types, custom parsing, and enum parsing without building a bunch of special-case classes.

Another good design decision is keeping validation attached to the argument itself with `validate(...)`. I think that made ranges, choices, and regex support feel a lot cleaner and kept the scenarios from turning into a mess.

### Bad design decisions

One bad design decision is that the error handling still feels a little broader than it should in some places. We do have `ArgumentException`, which helps, but some of the failures could still be more specific and cleaner.

Another bad design decision is that the separation between the argument system and the rest of the library is not perfectly clean yet. The argument system itself is simple, but the scenarios still end up doing some structure-related work that ideally would live more naturally on the command side.

### One good decision in my teammate’s system

One good design decision in the command system is separating command structure from parsed values. Having `Command` and `ParsedArgs` as different things was a good call because it makes the roles clearer and helps typed extraction feel more natural.

### One bad decision in my teammate’s system

One bad design decision in the command system is that it still feels a little awkward for some of the more expanded features. It works for the current cases, but stuff like defaults and subcommands still does not feel as smooth as it probably should.

## Team Review

One design decision we still do not totally agree on is how much validation responsibility should live in the argument system versus the command system. There is a case for keeping more validation directly on arguments, but there is also a case for moving some of that logic outward once command structure is involved.

One design concern we both agree on is that the overall design works, but some parts still feel a little forced as the feature set grows. The bigger concern is making sure new features fit into the API cleanly without making simple use cases way more annoying.
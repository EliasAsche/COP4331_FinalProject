Development Notes

For the argument system, the main goal was to keep it polymorphic and not tie it to one specific type. That is why the core design is built around ArgumentType<T> instead of hardcoding logic for stuff like int, double, or LocalDate. That made it easier to support custom parsing and then later add enum support too.

Another design choice was keeping validation attached to the argument itself. Instead of putting checks all over the scenarios, stuff like ranges, choices, and regex validation can live directly on the argument through validate(...). That kept the scenarios a lot cleaner.

I also added ArgumentException as a dedicated runtime exception for argument parsing and validation errors. The main point there was just to make the error handling more consistent so the rest of the library is not dealing with random generic runtime exceptions from the argument side.

One thing that still feels a little rough is the boundary between the argument system and the command system. The argument system handles parsing one value well, but some checks still depend on how the command side is structured, so that separation is better than before but not perfect.

Individual Review
Good design decisions

One good design decision in our argument system is using ArgumentType<T> as the main abstraction. That was nice because it let us support normal primitive types, custom parsing, and enums without hardcoding special cases all over the place.

Another good design decision is keeping validation attached to the argument itself with validate(...). I think that made stuff like ranges, choices, and regex feel a lot cleaner since the validation stays close to the thing being parsed.

Bad design decisions

One bad design decision is that our error handling is still kind of generic in some places. We do have ArgumentException, which helps, but some of the errors still feel a little too broad and could be more descriptive.

Another bad design decision is that the scenarios still do some checking that probably should be handled more by the command system. It works, but it makes the separation between the systems a little less clean than it should be.

One good decision in my teammate’s system

One good design decision in the command system is separating the command structure from the parsed values. I think having Command and ParsedArgs as different things was a good idea because it makes the roles a little clearer and helps with typed extraction.

One bad decision in my teammate’s system

One bad design decision in the command system is that it still feels a little underdeveloped for the MVP features. It works for the simpler cases, but things like defaults and subcommands do not feel super natural in the current design yet.

Team Review

One design decision we still kind of disagree on is how much validation responsibility should live in the argument system versus the command system. There is a case for both, and I do not think we have fully settled on the cleanest split yet.

One design concern we both agree on is that the current design probably needs to be cleaned up more before all the MVP features fit into it well, especially on the command side with defaults and subcommands.

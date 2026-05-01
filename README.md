




# command system

handles creation of command structures and multi-argument parsing.


# Development Notes

For the MVP I added a new ArgMetadata interface inside of the command class to keep what an argument is separate from how the command will actually use it. The Argument<T> we are using is just a parser and has no idea how the command will interact with it whether it be required, optional, position etc. The records implemented in the argmetadata carry all the per commmand role info. As mentioned before we declare position and named args separately so we can enforce our own rules for each. The user utilizes our library by creating an argument<T> and registering it either positional, named, or subcommand. Because we are using <T> instead of <?> this lets us return type <T> without needing a cast at call side. We achieve this by using a PArsedArgs container whih is produced when we call command.parse. The user passes in Arg <T> they registered which acts as a typed key. For error handling we created a CommandException to follow suit with Argument exception. This is to show bad user input, arg exception vs library mistakes, command exception. This allows us to create the user friendly error messages we have been talking about for this proj. 

# Individual Review (Command Lead)

Good designs decisions


The best design decisions in my opinion for my system is ParsedArgs.get(Arg<T>) -> T. This utilizes the arg obj itself as a typed key instead of a string! This is critical. The caller uses the same Arg<T> reference they registered with which lets us pass the type through. Which allows us to have the scenarios avoid casting such as int left = result.get(left_Arg). 

The second good design decision was the use of a SEALED argmetadata that allows us to maintain single responsibility for how a command uses a specific arg. Because a arg<int> can be a required position in one ommand but then an option arg in another means that the arg and the way it is used should be handled separately. 

# Not so good design decisions 

The 4 parameter named(arg<T>, list, T, T) is pretty ugly. It is hard for even me to remember which goes where so having it in a more organized and less quickly put together fashion is a good design consideration for the future. 

The second not so great design decision would be the fact that we need name-uniqueness checks for the fluent builder. 

# Good design decision from argument system 

Arg.enumeration(class<E>) is a polymorphic solution to any enum requirement. Thsi allows a user defined enum to work without the lib needing to have a dedicated class per each enum type which is a huge win. And it basically boils down to becoming just another ArgType<T>.

# Not so good design decision

The not so good part about the arg system is once again our exception catching and this also translates to my system. We both got a bit lazy here. Specifically in his system the .custom function rethrows the RunetimeException as a invalidcustom value which drops the reasioning behind the original exception. 



#team design 

The only major design decision we disagreed on was where the defaults belong. Right now I was the one who implemented defaults on the command side but it could also live on the args side though that might be a problem because some args might want different defaults values. We ended up sticking on the command side but it is a design decision that could probably go either way. 


# One design choice we agre could be improved 

The unchecked cast I implement in parsedargs.get is not great. It is a "Safe" cast but since you cant prove this at compile time its not the best and we wonder if there is a better way to code this. 


# Argument parsing polymorphism 

The way we handle polymorphism is through one tiny interface, ArgumentType<T>, which is just String -> T. Everything plugs into that. INTEGER, DOUBLE, STRING, BOOLEAN are just constants of ArgumentType<T>, enumeration(class<E>) returns an ArgumentType<E>, and custom(Function<String,T>) takes a user lambda and hands back an ArgumentType<T>. We thought about going the inheritance route with an AbstractArgumentType and an IntegerArgument, StringArgument etc but there was nothing actually shared between them and it would have just been ceremony. A lambda is enough to satisfy the contract so making a class for each type felt like overhead for no reason. Argument<T> ends up being a thin wrapper around one ArgumentType<T> plus a name and a list of validations. Adding a new type does not mean touching Argument or Command or ParsedArgs which is a really nice property to have.


# Parse don't validate 

We tried to follow the parse don't validate idea. The conversion from String to T happens once inside the ArgumentType and after that the value just is the type, so nothing downstream has to ask "is this actually an int". Extra rules are attached straight onto the Argument with .validate(predicate, message) and they run inside Argument.parse right after the type parse, so a single Argument<T> already represents a parsed and validated value. We did not build a separate validator subsystem and we don't think we needed one.

For numeric ranges we made one generic helper instead of a class per numeric type. Arguments.range(min, max) is generic over <T extends Comparable<T>> so the same helper works for Integer, Double, Long, BigDecimal, even strings if you wanted lexicographic ranges. No IntRange, DoubleRange duplication.

We also avoided the StringChoicesArgument explosion thing. Choices is just a Predicate<String> from Arguments.choices("a","b","c") that you hook in with .validate, not a class. Same for regex. And if a user wants something totally custom they pass their own Predicate<T> with their own message and it works the same way without us having to add anything to the lib.


# Command parsing and extraction 

Command.parse(String) runs the input through Input which is our sealed Value tokenizer (Literal, QuotedString, SingleFlag, DoubleFlag) and that gives us back a BasicArgs. From there Command matches positionals in order and resolves the named flags by looking each one up in the alias map. Subcommands are just a recursive parse on the child Command with the remaining BasicArgs.

The type-safe extraction part is what I already mentioned earlier with ParsedArgs.get(Arg<T>) -> T. The Arg ref is the key so the call site is int left = result.get(leftArg) with no cast. The cast inside ParsedArgs is unchecked but it is sound because the only thing that ever puts a value in there is Command.parse after running it through that argument's own typed parser. Subcommand identity comes back separately as result.subcommand().


# Does argument need to know its own name 

Yes but only for one reason and that reason is error messages. Argument.parse builds messages like "Invalid <name>: ..." so Argument carries its name for that. It does not know whether it is positional, named, required, optional, has a default, or has aliases. All of that role info lives on the command side in the metadata records (PositionalMetadata, NamedMetadata). That is exactly why an Arg<Integer> can be a required positional in one command and an optional named in another without changing the argument itself. So Argument stays String -> T plus validation and Command is the surrounding structure, which is what we wanted.


# State 

We know the rubric prefers one NameAndArgument list over splitting them. What we ended up with is a List<PositionalMetadata<?>> for positionals (positionals are ordered by index so a list felt right), a LinkedHashSet<NamedMetadata<?>> for the named slots, and a LinkedHashMap<String, NamedMetadata<?>> from each alias to the slot it points at. We kept positional and named separate because they really do behave differently. positionals are ordered and care about a "min required" cutoff, named are unordered, addressed by alias, and can have a present default for bare flags. Putting them in one list would have meant adding a discriminator field to every entry and we did not love that. The alias map is not a second source of truth either, every alias just points at the same NamedMetadata instance that already lives in namedSlots so they cannot drift apart.

Mutability is only a construction time thing. The collections inside Command only get touched while you are calling positional / named / subcommand. Once parse runs nothing in Command is rewritten. ParsedArgs is built once and only exposes the typed getter, the internal map never leaves. Argument's validation list is append only during setup. We thought about making a separate "compiled" immutable command type but since all of the mutation is already construction only and we don't expose any mutators after parse it felt like extra code for no gain.


# How we represent errors 

We use two exception types and we try to keep the lib from ever throwing a raw RuntimeException or catching RuntimeException / Exception. ArgumentException is for bad user input things like a parse failure, validation failure, missing required arg, unknown flag, too many positionals. CommandException is for library misuse by the dev calling our API like a duplicate arg name, mixing subcommands with positionals/named, an empty alias list, asking ParsedArgs for something that was never parsed.

We split them on purpose. ArgumentException is "the end user typed something wrong" and is what you would actually surface as a friendly message in an app. CommandException is "the developer wired the command up wrong" and should blow up loudly while you are still building the thing. Both extend RuntimeException so the public API stays unchecked but inside the lib we only throw our own types. The one place this falls down is the one we already called out earlier, Arguments.custom lets a user lambda's RuntimeException escape and that drops the cause. The fix we want to make is wrapping it in an ArgumentException using the (message, cause) constructor we already have so the original reason stays attached.


# Api shape 

The whole api basically comes down to three things, Argument.of, Command.of, and ParsedArgs.get. The simple case is one line per registration and that is it.

```
var left = Argument.of("left", Arguments.INTEGER);
var right = Argument.of("right", Arguments.INTEGER);
var cmd = Command.of("mul").positional(left).positional(right);
var result = cmd.parse(input);
int product = result.get(left) * result.get(right);
```

No builders, no config objects, no annotations to deal with. Anything optional layers on without changing the simple shape, defaults are positional(arg, default) or named(arg, names, default, presentDefault), validation is arg.validate(predicate, message), subcommands are .subcommand(child). You can pretty much read what a command looks like just from the chain of calls which is what we were going for.


# Scenarios 

All of the MVP scenarios live in CommandScenarios.java and the matching test file. mul is two required positional ints. div is two required named doubles. echo is a single positional with a default value. search is a positional plus a named boolean with an absent default and a present default registered under two aliases (-i and --case-insensitive). dispatch is the subcommand one with static vs dynamic where each subcommand has its own typed positional and we pull the branch out with result.subcommand() and the value with result.get(arg). The validation side of things, range across int/double, choices, regex, custom predicates, enums, all gets exercised in ArgumentScenariosTests through Arguments.range, .choices, .regex, .validate, and Arguments.enumeration.



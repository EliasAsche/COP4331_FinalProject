




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

# good design decision from arg system 

Arg.enumeration(class<E>) is a polymorphic solution to any enum requirement. Thsi allows a user defined enum to work without the lib needing to have a dedicated class per each enum type which is a huge win. And it basically boils down to becoming just another ArgType<T>.

# Not so good design decision

The not so good part about the arg system is once again our exception catching and this also translates to my system. We both got a bit lazy here. Specifically in his system the .custom function rethrows the RunetimeException as a invalidcustom value which drops the reasioning behind the original exception. 



#team design 

The only major design decision we disagreed on was where the defaults belong. Right now I was the one who implemented defaults on the command side but it could also live on the args side though that might be a problem because some args might want different defaults values. We ended up sticking on the command side but it is a design decision that could probably go either way. 


# one design choice we agre could be improved 

The unchecked cast I implement in parsedargs.get is not great. It is a "Safe" cast but since you cant prove this at compile time its not the best and we wonder if there is a better way to code this. 

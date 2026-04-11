# Command System

Handles creation of creation command structures and multi-argument parsing.

## Development Notes

We declare positional and named arguments separately, single responsibility. The user creates arguments using Argument<T> and then registers argument type, i.e. positional or named. Using Argument<T> over <?> allows us to create a typed key which we can move around without needing to recast. ParsedArgs acts as a type-safe container. We create a ParsedArgs instance in the .parse command in command.java, this allows us to later call .get which allows us to pass through Arugment<T> without ever needing to re-cast. We return map string object to fit testing criteria. 

## PoC Design Analysis


### Individual Review (Command Lead)


Using Argument<T> instead of <?> is our strongest design choice. IT allows the compiler to enforce the types with no risk of accidentally calling a getString on a Int. One not so great part of our design is the lack of detailed and well place exceptions throughout both the Command and Argument systems. In the lectue Prof showed us how this could be done better and we will try and implement it in the MVP. 

### Individual Review (Argument Lead)

The integration between Argument and Command is simple because we just have to give Argument<T> to the Command. The only issue and something we will try and work on in the future is the cast in ParsedArgs as this could lead to problems down the line. 

### Team Review

We are still figuring out how to delegate responsibilities and future proofing our systems. Currently there are some gaps in both te argument side and the command side which we will have to reconcile in the near future. 
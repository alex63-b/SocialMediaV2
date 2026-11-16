The factory works this way:

It receives a property object that represent the properties of the repositories that are about to be loaded, and it keeps those properties.

After that, to create a user repository, the program loads the mode of the repository(file,memory or database), and then it creates the
repository in the desired form, using the validator and the properties that the caller gave to the constructor.

If the mandatory properties for the creation of the repository aren't there, then the program will throw a ConfigException.

The creation of the friendship repository is similar, but the function also needs the user repository to potentially connect it to the
new friendship repository.

For changing to the database mode, you only need to change the repository.type parameter from the config file to DATABASE, and it will
start the application on database mode.

For changing to memory mode, you just need to change the type parameter to MEMORY in the config file.


If one of the files used by the repositories doesn't exist, it is created and the repository will start with no entries in it, because
the file is empty.

If one of the files used by the repositories has a corrupt line, then the program will print the corrupt lines of the file in the console,
and the corrupt lines will not be loaded in the repository. I chose this behavior because it is easier to fix up a corrupted file if all the
invalid lines are shown, as you know every line that is wrong, and the application will still be able to run without a file full of correct data.


SOLID Principles:

SRP(Single Responsibility Principle): The classes in the project all have a defined purpose, and only one reason to change. For example, the
main modeling classes(User and Friendship) only have the purpose to represent that concept, the repositories are separated so they have a single
responsibility(Repository to define what is a repository, MemoryRepository to define a repository in memory, etc.).

Also, the classes in service and UI are separated so they don't have more than one responsibility, and the factory is separated into
two classes exactly so that the Property generation is separated from the Repository generation, so that they have a single responsibility.

OCP(Open Closed Principle): All the layers in this application have been made so that they are as extensible as possible, with a lot of classes
that are extended, like the 3 layers of the repository that can be extended. Or, the domain objects which inherit everything from Entity, and
these abstract classes make adding features without modifying existing code pretty easy.

However, in the factory method, to add a new persistence type, you need to modify the code in the RepositoryType enum and the RepositoryFactory class
to add that new persistence type, but I kept this structure because actively modifying the retention type is easy, as you only have to change
the config file.

If we instead had created an interface for the factory and an implementation for each type of repository, the classes would be easier to extend
so that we create new persistence types, but the catch is that we still need to make a conditional clause to change the repository in another
place, which would still violate OCP. Still, the second method is better if we solely look at the OCP, but it also has the disadvantage that
it creates a lot of classes which still need switching.

LSP(Liskov Substitution Principle): As there is a lot of inheritance in this project, you can substitute in most cases a class with a subclass,
but the FileRepository class cannot be a substitute for superclasses, because that class is abstract. Otherwise, this principle is respected
everywhere in the project.

ISP(Interface Segregation Principle): In this project, the interfaces for every layer don't have any more functions than needed, and there
are very few interfaces anyway, so this principle is respected.

DIP(Dependency Inversion Principle): Everywhere in this project, I depend on interfaces so that this principle is respected,
though there are a few places where there are necessary dependencies on classes, not interfaces, like how the User repositories depend
on the User class, as that is a model for a user in my application and the whole purpose of the user repository is to handle Users.

Design patterns used in this project:

Template Method:
Used in FileRepository with the functions lineToEntity and entityToLine, which are defined in a subclass.

Strategy:
Used in FileRepository as the User and Friendship repositories are concrete strategies for FileRepository.
Used in MemoryRepository as FileRepository and the 2 concrete repositories are strategies for MemoryRepository.
Used in Repository as MemoryRepository and the 2 concrete database repositories are strategies for Repository.

Also, the strategy pattern can be implemented in the Service classes or other class structures where there currently would only
be a single strategy for the superclass.

Observer:
Used the Friendship repositories as observers and the User repositories as observables, so that the friendships can be modified when the user
list is changed, without too much coupling.

Simple Factory:
Used in PropertyFactory and in RepositoryFactory, to generate properties and repositories respectively.

------------------------------------------------------------ Laboratory 4 --------------------------------------------------------------------------------------

Some of the replaced loops are:

1. The loop in class MemoryRepository, in the method findAll(), that was iterating through the list of objects, is now a stream, which
uses filter to filter the unwanted entries, and a toList() to return the list of filtered items.

2. The loop in class FriendshipHelper, in the method updateHelper(formerly found in the classes FriendshipFileRepository and
ObserverMemoryFriendshipRepository), that was iterating through the list of friendships to keep the integrity of the repository,
is now made of 2 streams.

The first stream deletes the friendships which have missing users, and it does so by using a map to identify which users catch the exception. Then,
the Optional objects are filtered so only the existing ones remain, then they are mapped back to their ID's, and then the entries with those ID's
are deleted.

The second stream just goes through every user and modifies the data if it is necessary.

3. The loop in class BasicUserService, function setNextID(), that was iterating through the users to find the maximum ID, is now a stream,
which uses max() with a comparator to get the user with the maximum ID, and then get its ID.

4. The loop in class BasicFriendshipService, function getMostSociableCommunity, that was initializing the adjacency matrix for
the Roy-Warshall, is replaced by 2 nested streams. Both streams are IntStreams that simulate the for-in-for with a forEach, but the second stream
also has a filter to only initialize fields that are not on the diagonal or that are already initialized.

5. The loop in class FileRepository, function storeIntoFile(), that was iterating through the entities to convert them into file lines and then
write them, was replaced by a stream. That stream takes all the objects, uses a map() to map them to the lines, and then uses a toList to store
the result in a list of strings, and the strings are then written in the file.

Functional interfaces used:

- Validator<E>, which was used in many places as a simple class, but it was used in BasicUserService to add a new validation rule using andThen;
- Predicate<E>, which was used in findAll() as a parameter for a filter, and in many other situations where findAll() and filter() were used,
this functional interface was used;
- Function<R,T>, which was used in stream mapping, like in storeIntoFile to convert objects into lines, in the most sociable community to
make the final conversion from the ID's in the community to users, and in other places;
- Comparator<T>, which was used in getting the maximum ID from the repository of users to generate the next ID;

In the repository, the cases where the entities aren't found/other errors, the program throws an exception, so the return type of these functions
is void, so the discussion Optional<> vs null is pointless here.

Also, in this project I kept the Template design pattern, because giving the Function objects to the FileRepository would pose a big
disadvantage to the current code. In this project, the file repositories also have separate integrity rules that must be kept, and those
rules are managed by functions in their separate repositories. However, if we made the change so that the FileRepository receives those parameters,
we would still need those integrity rules somewhere, and implementing a new system with these restrictions would be much more difficult than
keeping the Template method, so I chose to keep it for simplicity.

There are still 2 places where loops are intact, and those places are:

1. The Roy-Warshall for loops in the most sociable community function are still there, as the algorithm modifies the matrix and gets the maximum
 length road along the way. Because lambda expressions don't let anyone modify outside variables as they are considered final, implementing those
 operations with streams would be much more difficult and unintuitive, so it would only add to the complexity of the program without added
 maintainability.

 2. The main loop that handles the UI in MainMenu is still there as we can't know what does the user want from the program, and for how many loops
 this loop will run. So, the streams aren't a good option for this, but there is also the fact that the data in the program is modified in the
 loop, so a stream will not be able to handle those operations normally.

------------------------------------------------------------ Laboratory 5 --------------------------------------------------------------------------------------

In this project, the database has ON DELETE CASCADE automatically, I chose this option because the repository must maintain its integrity. If I had
put the deletion of the friendships in the service when users are deleted, it means that:
1. If someone deletes things(through code or otherwise) from the repository layer directly or from the database, it will either be stopped(by the database),
or if there are other options in case of erasure, he will be able to wreck the integrity of the data from the friendship table.
2. If there are any other functionalities that need deletion, the service will have to handle the deletion of the friendships for every functionality,
which leads to repeating code.

In this case, the ON DELETE CASCADE will solve both of these issues, and it won't have any other big downsides, so I chose this option.
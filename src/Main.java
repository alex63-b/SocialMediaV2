import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.UserServices.ConsoleUserService;
import UI.Console.ConsoleMenu;
import UI.Console.MainMenu;

/**
 * This function configures the application and starts its console menu.
 * @throws ConfigException if the repository configuration is missing or invalid;
 * @throws RepositoryException if the repositories cannot be initialized;
 */
void main() {
    PropertyFactory propertyFactory = new PropertyFactory();
    RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("config/config.properties"));

    Validator<User> userValidator = new UserValidator();
    Repository<Long, User> userRepo = factory.createUserRepository(userValidator);

    Validator<Friendship> friendshipValidator = new FriendshipValidator();
    Repository<Pair<Long>, Friendship> friendshipRepo = factory.createFriendshipRepository(friendshipValidator);

    ConsoleUserService userService = new ConsoleUserService(userRepo);
    ConsoleFriendshipService friendshipService = new ConsoleFriendshipService(friendshipRepo, userRepo);

    ConsoleMenu menu = new MainMenu(userService, friendshipService);
    menu.mainLoop();
}

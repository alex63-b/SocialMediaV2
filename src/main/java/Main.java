import Domain.Objects.Friendship;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendshipValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;
import Repository.Exceptions.RepositoryException;
import Service.Factories.ServiceFactory;
import Service.FriendshipServices.BasicFriendshipService;
import Service.UserServices.BasicUserService;
import UI.Factory.ConsoleMenuFactory;
import util.factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import UI.Console.ConsoleMenu;

/**
 * This function configures the application and starts its console menu.
 * @throws ConfigException if the repository configuration is missing or invalid;
 * @throws RepositoryException if the repositories cannot be initialized;
 */
void main() {
    PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
    Properties properties = propertyFactory.propertiesFromConfigFile("config/config.properties");
    RepositoryFactory factory = new RepositoryFactory(properties);

    Validator<User> userValidator = new UserValidator();
    Repository<Long, User> userRepo = factory.createUserRepository(userValidator);

    Validator<Friendship> friendshipValidator = new FriendshipValidator();
    Repository<OrderedPair<Long>, Friendship> friendshipRepo = factory.createFriendshipRepository(friendshipValidator);

    ServiceFactory serviceFactory = new ServiceFactory(properties);

    BasicUserService userService = serviceFactory.createUserService(userRepo);
    BasicFriendshipService friendshipService = serviceFactory.createFriendshipService(friendshipRepo, userRepo);

    ConsoleMenu menu = ConsoleMenuFactory.createMenu(userService,friendshipService);
    menu.mainLoop();
}

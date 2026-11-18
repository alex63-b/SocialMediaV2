package Service.Factories;

import Domain.Objects.Friendship;
import Repository.Exceptions.RepositoryException;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Repository.AbstractRepositories.PagingRepository;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;
import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.FriendshipServices.PageFriendshipService;
import Service.UserServices.BasicUserService;
import Service.UserServices.ConsoleUserService;
import Service.UserServices.PageUserService;

import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Creates user and friendship services matching the repository configuration.
 */
public class ServiceFactory {
    private final Properties properties;
    private final Map<ServiceType, Function<Repository<Long, User>, BasicUserService>> userServiceFactory;
    private final Map<ServiceType, BiFunction<Repository<OrderedPair<Long>, Friendship>, Repository<Long,User>, BasicFriendshipService>> friendshipServiceFactory;
    /**
     * Creates a service factory from application properties.
     *
     * @param properties the application configuration
     */
    public ServiceFactory(Properties properties){
        this.properties = properties;
        userServiceFactory = Map.of(
                ServiceType.CONSOLE, (userRepository) -> {
                    if(!(userRepository instanceof PagingRepository<Long, User>)){
                        return new ConsoleUserService(userRepository);
                    }
                    else{
                        throw new ConfigException("The service doesn't have the same type as the repository!");
                    }
                },
                ServiceType.PAGE, (userRepository) -> {
                    if(userRepository instanceof PagingRepository<Long, User>){
                        try {
                            if(properties.getProperty("db.pageSize") == null){
                                throw new ConfigException("The page size doesn't exist in the configuration file!");
                            }
                            int pageSize = Integer.parseInt(properties.getProperty("db.pageSize"));
                            if(pageSize <= 0){
                                throw new ConfigException("The page size must be at least 1!");
                            }
                            return new PageUserService((PagingRepository<Long, User>) userRepository, pageSize);
                        }
                        catch (NumberFormatException e){
                            throw new ConfigException("The page size must be a number!");
                        }
                    }
                    else{
                        throw new ConfigException("The service doesn't have the same type as the repository!");
                    }
                }
        );
        friendshipServiceFactory = Map.of(
                ServiceType.CONSOLE, (friendshipRepository, userRepository) -> {
                    if(!(friendshipRepository instanceof PagingRepository<OrderedPair<Long>, Friendship>)){
                        return new ConsoleFriendshipService(friendshipRepository,userRepository);
                    }
                    else{
                        throw new ConfigException("The service doesn't have the same type as the repository!");
                    }
                },
                ServiceType.PAGE, (friendshipRepository, userRepository) -> {
                    if(friendshipRepository instanceof PagingRepository<OrderedPair<Long>, Friendship>){
                        try {
                            if(properties.getProperty("db.pageSize") == null){
                                throw new ConfigException("The page size doesn't exist in the configuration file!");
                            }
                            int pageSize = Integer.parseInt(properties.getProperty("db.pageSize"));
                            if (pageSize <= 0) {
                                throw new ConfigException("The page size must be at least 1!");
                            }
                            return new PageFriendshipService((PagingRepository<OrderedPair<Long>, Friendship>) friendshipRepository, userRepository, pageSize);
                        }
                        catch (NumberFormatException e){
                            throw new ConfigException("The page size must be a number!");
                        }
                    }
                    else{
                        throw new ConfigException("The service doesn't have the same type as the repository!");
                    }
                }
        );
    }
    /**
     * Creates the user service compatible with the supplied repository.
     *
     * @param userRepo the user repository
     * @return a console or paged user service
     * @throws ConfigException if the repository and configured service types are incompatible
     */
    public BasicUserService createUserService(Repository<Long, User> userRepo){
        ServiceType serviceType = ServiceType.CONSOLE;
        if(userRepo instanceof PagingRepository<Long, User>){
            serviceType = ServiceType.PAGE;
        }
        return userServiceFactory.get(serviceType).apply(userRepo);
    }
    /**
     * Creates the friendship service compatible with the supplied repositories.
     *
     * @param friendshipRepo the friendship repository
     * @param userRepo       the user repository
     * @return a console or paged friendship service
     * @throws ConfigException if the repositories and configured service types are incompatible
     */
    public BasicFriendshipService createFriendshipService(Repository<OrderedPair<Long>, Friendship> friendshipRepo, Repository<Long, User> userRepo){
        ServiceType serviceType = ServiceType.CONSOLE;
        if(friendshipRepo instanceof PagingRepository<OrderedPair<Long>, Friendship>){
            serviceType = ServiceType.PAGE;
        }
        return friendshipServiceFactory.get(serviceType).apply(friendshipRepo, userRepo);
    }
}

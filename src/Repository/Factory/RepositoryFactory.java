package Repository.Factory;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.Validator;
import Repository.ConcreteRepositories.*;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * This class creates repositories according to the supplied configuration properties.
 */
public class RepositoryFactory {
    private Repository<Long,User> userRepository = null;
    private final Properties properties;
    private final Map<RepositoryType, Function<Validator<User>,Repository<Long,User>>> userFactory;
    private final Map<RepositoryType, Function<Validator<Friendship>,Repository<Pair<Long>,Friendship>>> friendshipFactory;
    /**
     * This function creates a repository factory using the specified properties, and initializes the functional maps that
     * convert the repository types into factories.
     * @param properties the configuration properties used to select and configure repositories;
     */
    public RepositoryFactory(Properties properties){
        this.properties = properties;
        this.userFactory = Map.of(
                RepositoryType.MEMORY, MemoryUserRepository::new,
                RepositoryType.FILE, (validator) -> {
                    if(properties.getProperty("file.users") == null){
                        throw new ConfigException("The configuration file doesn't have the user file path!");
                    }
                    return new UserFileRepository(validator,properties.getProperty("file.users"));
                },
                RepositoryType.DATABASE,(validator) -> {
                    validateDBParameters();
                    return new UserDBRepository(validator,properties.getProperty("db.url"),
                            properties.getProperty("db.user"),properties.getProperty("db.password"));
                });
        this.friendshipFactory = Map.of(
                RepositoryType.MEMORY,(validator) -> {
                    if(userRepository instanceof MemoryUserRepository userRepo) {
                        return new MemoryFriendshipRepository(userRepo, validator);
                    }
                    else{
                        throw new ConfigException("The repository types are different!");
                    }
                },
                RepositoryType.FILE,(validator) -> {
                    if(properties.getProperty("file.friendships") == null){
                        throw new ConfigException("The configuration file doesn't have the friendship file path!");
                    }
                    if(userRepository instanceof UserFileRepository userRepo) {
                        return new FriendshipFileRepository(userRepo,validator,properties.getProperty("file.friendships"));
                    }
                    else{
                        throw new ConfigException("The repository types are different!");
                    }
                },
                RepositoryType.DATABASE,(validator) -> {
                    validateDBParameters();
                    return new FriendshipDBRepository(validator,properties.getProperty("db.url"),
                            properties.getProperty("db.user"),properties.getProperty("db.password"));
                }
        );
    }

    private RepositoryType getRepositoryType(){
        String repoType = properties.getProperty("repository.type");
        if(repoType == null){
            throw new ConfigException("The repository type is empty!");
        }
        try {
            return RepositoryType.valueOf(repoType.toUpperCase().trim());
        }
        catch (IllegalArgumentException e) {
            throw new ConfigException("This repository type doesn't exist!");
        }
    }
    private void validateDBParameters(){
        if(properties.getProperty("db.url") == null){
            throw new ConfigException("The configuration file doesn't have the database URL!");
        }
        if(properties.getProperty("db.user") == null){
            throw new ConfigException("The configuration file doesn't have the database user!");
        }
        if(properties.getProperty("db.password") == null){
            throw new ConfigException("The configuration file doesn't have the database password!");
        }
    }
    /**
     * This function creates a user repository for the configured storage type.
     * @param validator the validator used for users;
     * @return the configured user repository;
     * @throws ConfigException if required configuration is missing or the repository type is unsupported;
     */
    public Repository<Long,User> createUserRepository(Validator<User> validator){
        RepositoryType repositoryType = getRepositoryType();
        if(userFactory.containsKey(repositoryType)) {
            userRepository = userFactory.get(repositoryType).apply(validator);
            return userRepository;
        }
        else{
            throw new ConfigException("This configuration mode doesn't exist!");
        }
    }

    /**
     * This function creates a friendship repository for the configured storage type, using the user repository in the class.
     * @param friendshipValidator the validator used for friendships;
     * @return the configured friendship repository;
     * @throws ConfigException if required configuration is missing, repository types do not match, the user repository hasn't been
     * initialized yet, or the configured type is unsupported;
     */
    public Repository<Pair<Long>,Friendship> createFriendshipRepository(Validator<Friendship> friendshipValidator){
        RepositoryType repositoryType = getRepositoryType();
        if(userRepository == null){
            throw new ConfigException("You can't create a friendship repository without an user repository!");
        }
        if(friendshipFactory.containsKey(repositoryType)){
            return friendshipFactory.get(repositoryType).apply(friendshipValidator);
        }
        else{
            throw new ConfigException("Repository type not found!");
        }
    }
}

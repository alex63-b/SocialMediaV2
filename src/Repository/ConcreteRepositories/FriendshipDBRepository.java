package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class stores friendships in a database and provides repository operations for them.
 */
public class FriendshipDBRepository implements Repository<Pair<Long>, Friendship> {
    private final String url, user, password;
    private final Validator<Friendship> friendshipValidator;

    /**
     * This function creates a friendship database repository with the specified connection settings.
     * @param friendshipValidator the validator used for friendships;
     * @param url the database connection URL;
     * @param user the database username;
     * @param password the database password;
     */
    public FriendshipDBRepository(Validator<Friendship> friendshipValidator, String url, String user, String password){
        this.friendshipValidator = friendshipValidator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Returns the number of friendships in the repository.
     * @return the number of stored friendships;
     */
    @Override
    public int size() {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Returns the friendships in the repository that are accepted by the filter.
     * @param filter a predicate which acts as a filter for friendships;
     * @return the friendships accepted by the filter;
     */
    @Override
    public List<Friendship> findAll(Predicate<Friendship> filter) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Finds a friendship by its identifier, the pair of user IDs.
     * @param id the pair of user IDs identifying the friendship;
     * @return the friendship with the specified identifier, or {@code Optional.empty()} if it does not exist;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<Friendship> findElement(Pair<Long> id) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Validates and adds a friendship to the repository.
     * @param new_friendship the friendship to add;
     * @return {@code Optional.empty()} if a friendship with the same identifier already exists, or the added friendship;
     * @throws Domain.Validation.ValidationException if the friendship does not satisfy its validation rules;
     * @throws Repository.Exceptions.RepositoryException if the friendship or its identifier is null;
     */
    @Override
    public Optional<Friendship> addElement(Friendship new_friendship) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Removes a friendship from the repository by its identifier.
     * @param id the pair of user IDs identifying the friendship;
     * @return {@code Optional.empty()} if no friendship with the identifier exists, or the removed friendship;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<Friendship> deleteElement(Pair<Long> id) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Validates and replaces an existing friendship.
     * @param new_friendship the friendship containing the replacement data;
     * @return the friendship if no friendship with its identifier exists, or {@code Optional.empty()} on success;
     * @throws Domain.Validation.ValidationException if the friendship does not satisfy its validation rules;
     * @throws Repository.Exceptions.RepositoryException if the friendship or its identifier is null;
     */
    @Override
    public Optional<Friendship> modifyElement(Friendship new_friendship) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }
}

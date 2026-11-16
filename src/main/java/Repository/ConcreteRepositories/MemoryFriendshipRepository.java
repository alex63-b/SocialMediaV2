package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.MemoryRepository;
import Repository.AbstractRepositories.ObservableUserRepository;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.FriendshipHelper;
import Repository.Observer.FriendshipObserver;

import java.util.Optional;

/**
 * Stores friendships in memory and synchronizes them with changes to the user repository.
 */
public class MemoryFriendshipRepository extends MemoryRepository<Pair<Long>, Friendship> implements FriendshipObserver {
    private final ObservableUserRepository user_repo;
    private final FriendshipHelper friendshipHelper;
    /**
     * Creates an in-memory friendship repository associated with an observable user repository.
     *
     * @param user_repo the observable repository used to find and track users
     * @param validator the validator used for friendships
     */
    public MemoryFriendshipRepository(ObservableUserRepository user_repo, Validator<Friendship> validator) {
        super(validator);
        this.user_repo = user_repo;
        this.friendshipHelper = FriendshipHelper.getFriendshipHelper();
        user_repo.addObserver(this);
    }

    /**
     * Removes a friendship from this repository by its identifier.
     *
     * @param id the pair of user IDs identifying the friendship
     * @return {@code Optional.empty()} if no friendship with the identifier exists, or the removed friendship
     */
    @Override
    public Optional<Friendship> deleteElement(Pair<Long> id) throws RepositoryException {
        return super.deleteElement(id);
    }

    /**
     * Adds a friendship and verifies its users against the user repository.
     *
     * @param new_element the friendship to add
     * @return {@code Optional.empty()} if a friendship with the same identifier already exists, or the added friendship
     * @throws RepositoryException if the friendship or its identifier is {@code null}, or either user is missing from
     *                             the user repository or differs from the stored user
     * @throws ValidationException if the friendship does not satisfy its validation rules
     */
    @Override
    public Optional<Friendship> addElement(Friendship new_element) throws RepositoryException, RepositoryException {
        Optional<Friendship> result = super.addElement(new_element);
        if(result.isEmpty()){
            return result;
        }
        friendshipHelper.validateRelations(new_element, user_repo, this);
        return result;
    }

    /**
     * Synchronizes stored friendships with user repository changes. Friendships for removed users are deleted, and
     * the user data in remaining friendships is refreshed.
     */
    @Override
    public void update() {
        friendshipHelper.updateHelper(user_repo,this);
    }
}

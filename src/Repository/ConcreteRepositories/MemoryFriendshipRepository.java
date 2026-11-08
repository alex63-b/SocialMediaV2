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
 * This class stores friendships in memory and synchronizes them with user repository changes.
 */
public class MemoryFriendshipRepository extends MemoryRepository<Pair<Long>, Friendship> implements FriendshipObserver {
    private final ObservableUserRepository user_repo;
    private final FriendshipHelper friendshipHelper;
    /**
     * This function creates an in-memory friendship repository associated with an observable user repository.
     * @param user_repo the observable repository used to find and track users;
     * @param validator the validator used for friendships;
     */
    public MemoryFriendshipRepository(ObservableUserRepository user_repo, Validator<Friendship> validator) {
        super(validator);
        this.user_repo = user_repo;
        this.friendshipHelper = FriendshipHelper.getFriendshipHelper();
        user_repo.addObserver(this);
    }

    /**
     * This function removes a friendship and removes the corresponding friend links from both users.
     * @param id the pair of user IDs identifying the friendship;
     * @throws RepositoryException if the friendship does not exist;
     */
    @Override
    public Optional<Friendship> deleteElement(Pair<Long> id) throws RepositoryException {
        return super.deleteElement(id);
    }

    /**
     * This function validates a friendship's users and prevents duplicate relationships.
     * @param new_element the friendship whose relationship to validate;
     * @throws RepositoryException if the users are not present, do not match their stored records,
     *                             or already have a friendship;
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
     * This function synchronizes stored friendships with user repository changes.
     * Friendships for removed users are deleted and friendships are updated with current user data.
     */
    @Override
    public void update() {
        friendshipHelper.updateHelper(user_repo,this);
    }
}

package Repository.ConcreteRepositories;

import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.MemoryRepository;
import Repository.AbstractRepositories.ObservableUserRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Observer.FriendshipObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stores users in memory and notifies observers when the repository changes.
 */
public class MemoryUserRepository extends MemoryRepository<Long, User> implements ObservableUserRepository {
    private final List<FriendshipObserver> observers = new ArrayList<>();

    /**
     * Creates an observable in-memory user repository.
     *
     * @param validator the validator used for users
     */
    public MemoryUserRepository(Validator<User> validator) {

        super(validator);
    }

    /**
     * Registers an observer to receive user repository updates.
     *
     * @param new_observer the observer to register
     */
    @Override
    public void addObserver(FriendshipObserver new_observer) {

        observers.add(new_observer);
    }

    /**
     * Removes an observer from user repository notifications.
     *
     * @param deleted_observer the observer to remove
     */
    @Override
    public void removeObserver(FriendshipObserver deleted_observer) {

        observers.remove(deleted_observer);
    }

    /**
     * Notifies all registered observers about a repository update.
     */
    @Override
    public void notifyObservers() {
        observers.forEach(FriendshipObserver::update);
    }

    /**
     * Validates and adds a user, then notifies observers if the addition succeeds.
     *
     * @param new_element the user to add
     * @return {@code Optional.empty()} if a user with the same identifier already exists, or the added user
     * @throws RepositoryException if the user or its identifier is {@code null}
     * @throws ValidationException if the user does not satisfy its validation rules
     */
    @Override
    public Optional<User> addElement(User new_element) throws RepositoryException {
        Optional<User> result = super.addElement(new_element);
        if(result.isPresent()) {
            notifyObservers();
        }
        return result;
    }

    /**
     * Removes a user and notifies observers if a user was removed.
     *
     * @param id the identifier of the user to remove
     * @return {@code Optional.empty()} if no user with the identifier exists, or the removed user
     */
    @Override
    public Optional<User> deleteElement(Long id) throws RepositoryException {
        Optional<User> result = super.deleteElement(id);
        if(result.isPresent()) {
            notifyObservers();
        }
        return result;
    }

    /**
     * Replaces a user and notifies observers if the replacement succeeds.
     *
     * @param new_element the user containing the replacement data
     * @return the given user if its identifier is not in the repository, or {@code Optional.empty()} if it was replaced
     * @throws RepositoryException if the user or its identifier is {@code null}
     * @throws ValidationException if the user does not satisfy its validation rules
     */
    @Override
    public Optional<User> modifyElement(User new_element) throws RepositoryException {
        Optional<User> result = super.modifyElement(new_element);
        if(result.isEmpty()) {
            notifyObservers();
        }
        return result;
    }
}

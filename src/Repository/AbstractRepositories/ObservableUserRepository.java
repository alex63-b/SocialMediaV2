package Repository.AbstractRepositories;

import Domain.Objects.User;
import Repository.Observer.FriendshipObserver;

/**
 * This interface defines a user repository that notifies observers about changes.
 */
public interface ObservableUserRepository extends Repository<Long, User>{
    /**
     * This function registers an observer to receive repository updates.
     * @param new_observer the observer to register;
     */
    void addObserver(FriendshipObserver new_observer);

    /**
     * This function removes an observer from the repository's notifications.
     * @param deleted_observer the observer to remove;
     */
    void removeObserver(FriendshipObserver deleted_observer);

    /**
     * This function notifies all registered observers about a repository update.
     */
    void notifyObservers();
}

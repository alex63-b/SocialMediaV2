package Repository.Observer;

/**
 * This interface defines a friendship observer notified when a repository changes, that also has some default functions
 * ready to use.
 */
public interface FriendshipObserver {
    /**
     * This function handles a repository update notification.
     */
    void update();
}

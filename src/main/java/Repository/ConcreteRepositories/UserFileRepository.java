package Repository.ConcreteRepositories;

import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.FileRepository;
import Repository.AbstractRepositories.ObservableUserRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Observer.FriendshipObserver;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class stores users in a file and notifies observers when users change.
 */
public class UserFileRepository extends FileRepository<Long, User> implements ObservableUserRepository {
    private final List<FriendshipObserver> observers = new ArrayList<>();

    /**
     * This function creates a user repository backed by the specified file.
     * @param validator the validator used for users;
     * @param fileName the path to the user data file;
     * @throws RepositoryException if the file cannot be created or opened;
     */
    public UserFileRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * This function converts a file line into a user.
     * @param line the line to convert;
     * @return the user represented by the line;
     * @throws ValidationException if the line has an invalid number of fields or ID;
     */
    @Override
    protected User lineToEntity(String line) throws ValidationException {
        String[] split = line.split(",");
        try {
            if(split.length != 3){
                throw new ValidationException("A line in the file has the wrong number of fields!");
            }
            Long id = Long.parseLong(split[0]);
            return new User(id,split[1],split[2]);
        }
        catch (NumberFormatException e){
            throw new ValidationException("The ID in a line of the file is not valid!");
        }
    }

    /**
     * This function converts a user into a comma-separated file line.
     * @param entity the user to convert;
     * @return the line representing the user's ID, name, and first name;
     */
    @Override
    protected String entityToLine(User entity) {
        return entity.getId() + "," + entity.getName() + "," + entity.getFirstName() ;
    }

    /**
     * This function registers an observer to receive user repository updates.
     * @param new_observer the observer to register;
     */
    @Override
    public void addObserver(FriendshipObserver new_observer) {
        observers.add(new_observer);
    }

    /**
     * This function removes an observer from user repository notifications.
     * @param deleted_observer the observer to remove;
     */
    @Override
    public void removeObserver(FriendshipObserver deleted_observer) {
        observers.remove(deleted_observer);
    }

    /**
     * This function notifies all registered observers about a repository update.
     */
    @Override
    public void notifyObservers() {
        observers.forEach(FriendshipObserver::update);
    }

    /**
     * This function validates and adds a user, saves it, and notifies observers.
     * @param element the user to add;
     * @throws RepositoryException if a user with the same ID exists or the file cannot be written;
     * @throws ValidationException if the user does not satisfy its validation rules;
     */
    @Override
    public Optional<User> addElement(User element) throws RepositoryException {
        Optional<User> result = super.addElement(element);
        if(result.isPresent()) {
            notifyObservers();
        }
        return result;
    }

    /**
     * This function removes a user, saves the change, and notifies observers.
     * @param id the ID of the user to remove;
     * @throws RepositoryException if the user does not exist or the file cannot be written;
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
     * This function replaces a user, saves the change, and notifies observers.
     * @param element the user containing the replacement data;
     * @throws RepositoryException if the user does not exist or the file cannot be written;
     * @throws ValidationException if the user does not satisfy its validation rules;
     */
    @Override
    public Optional<User> modifyElement(User element) throws RepositoryException {
        Optional<User> result = super.modifyElement(element);
        if(result.isEmpty()) {
            notifyObservers();
        }
        return result;
    }
}

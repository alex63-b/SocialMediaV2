package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.FileRepository;
import Repository.AbstractRepositories.ObservableUserRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.FriendshipHelper;
import Repository.Observer.FriendshipObserver;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * This class stores friendships in a file and observes changes to the user repository.
 */
public class FriendshipFileRepository extends FileRepository<Pair<Long>, Friendship> implements FriendshipObserver {
    ObservableUserRepository user_repo;
    FriendshipHelper friendshipHelper;

    /**
     * This function creates a friendship repository backed by a file and registers it with the user repository.
     * @param user_repo the observable repository used to find and track users;
     * @param validator the validator used for friendships;
     * @param fileName the path to the friendship data file;
     * @throws RepositoryException if the file cannot be created or opened;
     */
    public FriendshipFileRepository(ObservableUserRepository user_repo, Validator<Friendship> validator, String fileName) {
        this.user_repo = user_repo;
        friendshipHelper = FriendshipHelper.getFriendshipHelper();
        super(validator, fileName);
        user_repo.addObserver(this);
    }

    /**
     * This function converts a file line into a friendship.
     * @param line the line to convert;
     * @return the friendship represented by the line;
     * @throws RepositoryException if the line's fields, user IDs, or date cannot be parsed;
     */
    @Override
    protected Friendship lineToEntity(String line) throws RepositoryException {
        String[] splitLine = line.split(",");
        try {
            if (splitLine.length != 3) {
                throw new RepositoryException("A line in the file has the wrong number of fields!", new IOException());
            }
            Long userId1 = Long.parseLong(splitLine[0]);
            Long userId2 = Long.parseLong(splitLine[1]);
            LocalDateTime date = LocalDateTime.parse(splitLine[2]);
            if(user_repo.findElement(userId1).isEmpty() || user_repo.findElement(userId2).isEmpty()){
                throw new RepositoryException("A user with the respective id couldn't be found!",new InvalidObjectException(""));
            }
            return new Friendship(user_repo.findElement(userId1).get(),user_repo.findElement(userId2).get(),date);
        }
        catch (NumberFormatException e){
            throw new RepositoryException("One of the numeric fields does not contain a valid number!",e.getCause());
        }
        catch (DateTimeParseException e){
            throw new RepositoryException("The date of the friendship isn't readable!",e.getCause());
        }
    }

    /**
     * This function converts a friendship into a comma-separated file line.
     * @param entity the friendship to convert;
     * @return the line representing the users' IDs and friendship date;
     */
    @Override
    protected String entityToLine(Friendship entity) {
        return entity.getFriend1().getId() + "," + entity.getFriend2().getId() + "," + entity.getDate();
    }

    /**
     * This function validates the relationship, adds a friendship, and links the users as friends.
     * @param new_element the friendship to add;
     * @throws RepositoryException if the users are not present, the relationship already exists,
     *                             or the file cannot be written;
     * @throws ValidationException if the friendship does not satisfy its validation rules;
     */
    @Override
    public Optional<Friendship> addElement(Friendship new_element) throws RepositoryException, RepositoryException {
        friendshipHelper.validateRelations(new_element,user_repo,this);
        return super.addElement(new_element);
    }

    /**
     * This function adds a friendship while loading the file and links its users as friends.
     * @param new_element the friendship to add;
     * @throws RepositoryException if the users are not present or the relationship already exists;
     * @throws ValidationException if the friendship does not satisfy its validation rules;
     */
    @Override
    protected Optional<Friendship> noStoreAddElement(Friendship new_element) throws RepositoryException, RepositoryException {
        friendshipHelper.validateRelations(new_element,user_repo,this);
        return super.noStoreAddElement(new_element);
    }

    /**
     * This function synchronizes stored friendships with user repository changes.
     * Friendships for removed users are deleted and friendships are updated with current user data.
     * @throws RepositoryException if a repository operation fails while synchronizing;
     */
    @Override
    public void update() {
        friendshipHelper.updateHelper(user_repo,this);
    }
}

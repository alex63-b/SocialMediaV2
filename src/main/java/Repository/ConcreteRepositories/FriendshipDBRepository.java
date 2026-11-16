package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.FriendshipHelper;

import java.security.InvalidParameterException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class stores friendships in a database and provides repository operations for them.
 */
public class FriendshipDBRepository implements Repository<Pair<Long>, Friendship> {
    private final String url, user, password;
    private final Validator<Friendship> friendshipValidator;
    private final Repository<Long,User> userRepo;
    private final FriendshipHelper helper;
    private void setFirstSize(){

    }
    /**
     * This function creates a friendship database repository with the specified connection settings.
     * @param friendshipValidator the validator used for friendships;
     * @param url the database connection URL;
     * @param user the database username;
     * @param password the database password;
     */
    public FriendshipDBRepository(Repository<Long,User> userRepo, Validator<Friendship> friendshipValidator, String url, String user, String password){
        this.userRepo = userRepo;
        this.friendshipValidator = friendshipValidator;
        this.url = url;
        this.user = user;
        this.password = password;
        this.helper = FriendshipHelper.getFriendshipHelper();
        setFirstSize();
    }

    /**
     * Returns the number of friendships in the repository.
     * @return the number of stored friendships;
     */
    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement statement = connection.prepareStatement("SELECT count(*) AS count FROM friendships;");
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        }
        catch (SQLException exception){
            throw new RepositoryException("The size script didn't work!",exception.getCause());
        }
    }

    /**
     * Returns the friendships in the repository that are accepted by the filter.
     * @param filter a predicate which acts as a filter for friendships;
     * @return the friendships accepted by the filter;
     */
    @Override
    public List<Friendship> findAll(Predicate<Friendship> filter) {
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships ORDER BY id1, id2");
            ResultSet set = statement.executeQuery();
            List<Friendship> userList = new ArrayList<>();
            while(set.next()){
                Long id1 = set.getLong("id1");
                Long id2 = set.getLong("id2");
                LocalDateTime date = LocalDateTime.of(set.getDate("friendshipDate").toLocalDate(), LocalTime.MIDNIGHT);
                if(userRepo.findElement(id1).isPresent() && userRepo.findElement(id2).isPresent()) {
                    Friendship newFriendship = new Friendship(userRepo.findElement(id1).get(), userRepo.findElement(id2).get(), date);
                    if (filter.test(newFriendship)) {
                        userList.add(newFriendship);
                    }
                }
                else{
                    throw new RepositoryException("The users with these ID's don't exist in the repository!",new InvalidParameterException());
                }
            }
            return userList;
        }
        catch (SQLException e){
            throw new RepositoryException("The connection to the friendship database couldn't be formed!",e.getCause());
        }
    }

    /**
     * Finds a friendship by its identifier, the pair of user IDs.
     * @param id the pair of user IDs identifying the friendship;
     * @return the friendship with the specified identifier, or {@code Optional.empty()} if it does not exist;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<Friendship> findElement(Pair<Long> id) {
        if(id == null){
            throw new RepositoryException("The id can't be null!",new InvalidParameterException());
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE id1=? AND id2=?");
            statement.setLong(1,id.getFirstElement());
            statement.setLong(2,id.getSecondElement());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Optional<User> user1 = userRepo.findElement(id.getFirstElement());
                Optional<User> user2 = userRepo.findElement(id.getSecondElement());
                if(user1.isPresent() && user2.isPresent()) {
                    LocalDateTime date = resultSet.getTimestamp("friendshipDate").toLocalDateTime();
                    return Optional.of(new Friendship(user1.get(), user2.get(), date));
                }
                else{
                    throw new RepositoryException("The users with these ID's don't exist in the repository",new InvalidParameterException());
                }
            }
            return Optional.empty();
        }
        catch(SQLException ex){
            throw new RepositoryException("The connection to the friendship database couldn't be formed!",ex.getCause());
        }
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
        if(new_friendship == null){
            throw new RepositoryException("The new friendship can't be null!", new InvalidParameterException());
        }
        if(findElement(new_friendship.getId()).isPresent()){
            return Optional.empty();
        }
        friendshipValidator.validate(new_friendship);
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships(id1, id2, friendshipDate) VALUES (?,?,?)");
            statement.setLong(1,new_friendship.getFriend1().getId());
            statement.setLong(2,new_friendship.getFriend2().getId());
            statement.setTimestamp(3, Timestamp.valueOf(new_friendship.getDate()));
            try {
                statement.executeUpdate();

                return Optional.of(new_friendship);
            }
            catch (SQLException e){
                throw new RepositoryException("The ID's of the users must exist in the user list!",e.getCause());
            }
        }
        catch (SQLException e){
            throw new RepositoryException("The connection to the friendship database couldn't be formed!",e.getCause());
        }
    }

    /**
     * Removes a friendship from the repository by its identifier.
     * @param id the pair of user IDs identifying the friendship;
     * @return {@code Optional.empty()} if no friendship with the identifier exists, or the removed friendship;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<Friendship> deleteElement(Pair<Long> id) {
        if(id == null){
            throw new RepositoryException("The ID must not be null!",new InvalidParameterException());
        }
        Optional<Friendship> foundFriendship = findElement(id);
        if(foundFriendship.isEmpty()){
            return Optional.empty();
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            Friendship oldFriendship = foundFriendship.get();

            PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id1=? AND id2=?");
            statement.setLong(1,id.getFirstElement());
            statement.setLong(2,id.getSecondElement());
            statement.executeUpdate();

            return foundFriendship;
        }
        catch (SQLException e){
            throw new RepositoryException("The connection to the friendship database couldn't be formed!",e.getCause());
        }
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
        if(new_friendship == null){
            throw new RepositoryException("The new friendship can't be null!", new InvalidParameterException());
        }
        if(new_friendship.getId() == null){
            throw new RepositoryException("The new friendship's ID can't be null!", new InvalidParameterException());
        }
        friendshipValidator.validate(new_friendship);
        if(findElement(new_friendship.getId()).isEmpty()){
            return Optional.of(new_friendship);
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET friendshipdate=? WHERE id1=? AND id2=?");
            statement.setTimestamp(1,Timestamp.valueOf(new_friendship.getDate()));
            statement.setLong(2,new_friendship.getFriend1().getId());
            statement.setLong(3,new_friendship.getFriend2().getId());
            try {
                statement.executeUpdate();
                return Optional.empty();
            }
            catch (SQLException e) {
                throw new RepositoryException("The date cannot be null!",e.getCause());
            }
        }
        catch (SQLException e){
            throw new RepositoryException("The connection to the friendship database couldn't be formed!",e.getCause());
        }
    }
}

package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.User;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class stores users in a database and provides repository operations for them.
 */
public class UserDBRepository implements Repository<Long, User> {
    private final String url, user, password;
    private final Validator<User> userValidator;
    private int size = 0;

    private void setFirstSize(){
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                size += 1;
            }
        }
        catch (SQLException exception){
            throw new RepositoryException("The size script didn't work!",exception.getCause());
        }
    }
    /**
     * This function creates a user database repository with the specified connection settings.
     * @param userValidator the validator used for users;
     * @param url the database connection URL;
     * @param user the database username;
     * @param password the database password;
     */
    public UserDBRepository(Validator<User> userValidator, String url, String user, String password){
        this.userValidator = userValidator;
        this.url = url;
        this.user = user;
        this.password = password;
        setFirstSize();
    }

    /**
     * Returns the number of users in the repository.
     * @return the number of stored users;
     */
    @Override
    public int size() {
        return size;
    }
    /**
     * Returns the users in the repository that are accepted by the filter.
     * @param filter a predicate which acts as a filter for users;
     * @return the users accepted by the filter;
     */
    @Override
    public List<User> findAll(Predicate<User> filter) {
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users ORDER BY id");
            ResultSet set = statement.executeQuery();
            List<User> userList = new ArrayList<>();
            while(set.next()){
                String name = set.getString("last_name");
                String firstName = set.getString("first_name");
                Long id = set.getLong("id");
                User newUser = new User(id,name,firstName);
                if(filter.test(newUser)) {
                    userList.add(newUser);
                }
            }
            return userList;
        }
        catch (SQLException e){
            throw new RepositoryException("SQL findAll error: " + e.getMessage(),e.getCause());
        }
    }

    /**
     * Finds a user by its identifier.
     * @param id the identifier of the user to find;
     * @return the user with the specified identifier, or {@code Optional.empty()} if it does not exist;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null or the SQL connection doesn't work;
     */
    @Override
    public Optional<User> findElement(Long id) {
        if(id == null){
            throw new RepositoryException("The ID can't be null!",new InvalidParameterException());
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id=?");
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                return Optional.of(new User(id,name,firstName));
            }
            return Optional.empty();
        }
        catch(SQLException ex){
            throw new RepositoryException("SQL findElement error: " + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Validates and adds a user to the repository.
     * @param new_element the user to add;
     * @return {@code Optional.empty()} if a user with the same identifier already exists, or the added user;
     * @throws Domain.Validation.ValidationException if the user does not satisfy its validation rules;
     * @throws Repository.Exceptions.RepositoryException if the user or its identifier is null;
     */
    @Override
    public Optional<User> addElement(User new_element) {
        if(new_element == null){
            throw new RepositoryException("The new user can't be null!",new InvalidParameterException());
        }
        if(findElement(new_element.getId()).isPresent()){
            return Optional.empty();
        }
        userValidator.validate(new_element);
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users(first_name, last_name) VALUES (?,?)");
            statement.setString(1,new_element.getFirstName());
            statement.setString(2,new_element.getName());
            statement.executeUpdate();
            size += 1;
            return Optional.of(new_element);
        }
        catch (SQLException e){
            throw new RepositoryException("SQL add error: " + e.getMessage(),e.getCause());
        }
    }

    /**
     * Removes a user from the repository by its identifier.
     * @param id the identifier of the user to remove;
     * @return {@code Optional.empty()} if no user with the identifier exists, or the removed user;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<User> deleteElement(Long id) {
        if(id == null){
            throw new RepositoryException("The ID for delete can't be null!",new InvalidParameterException());
        }
        Optional<User> foundUser = findElement(id);
        if(foundUser.isEmpty()){
            return Optional.empty();
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id=?");
            statement.setLong(1,id);
            statement.executeUpdate();
            size -= 1;
            return foundUser;
        }
        catch (SQLException e){
            throw new RepositoryException("SQL delete error: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * Validates and replaces an existing user.
     * @param new_element the user containing the replacement data;
     * @return the user if no user with its identifier exists, or {@code Optional.empty()} on success;
     * @throws Domain.Validation.ValidationException if the user does not satisfy its validation rules;
     * @throws Repository.Exceptions.RepositoryException if the user or its identifier is null;
     */
    @Override
    public Optional<User> modifyElement(User new_element) {
        if(new_element == null){
            throw new RepositoryException("The new user can't be null!",new InvalidParameterException());
        }
        if(new_element.getId() == null){
            throw new RepositoryException("The new user's ID can't be null!",new InvalidParameterException());
        }
        userValidator.validate(new_element);
        if(findElement(new_element.getId()).isEmpty()){
            return Optional.of(new_element);
        }
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name=?,last_name=? WHERE id=?");
            statement.setString(1,new_element.getFirstName());
            statement.setString(2,new_element.getName());
            statement.setLong(3,new_element.getId());
            statement.executeUpdate();
            return Optional.empty();
        }
        catch (SQLException e){
            throw new RepositoryException("SQL modify error: " + e.getMessage(), e.getCause());
        }
    }
}

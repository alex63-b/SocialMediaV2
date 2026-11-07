package Repository.ConcreteRepositories;

import Domain.Objects.Friendship;
import Domain.Objects.User;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;

import java.sql.*;
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
                Long ceva = resultSet.getLong(0);
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
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Finds a user by its identifier.
     * @param id the identifier of the user to find;
     * @return the user with the specified identifier, or {@code Optional.empty()} if it does not exist;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<User> findElement(Long id) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
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
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }

    /**
     * Removes a user from the repository by its identifier.
     * @param id the identifier of the user to remove;
     * @return {@code Optional.empty()} if no user with the identifier exists, or the removed user;
     * @throws Repository.Exceptions.RepositoryException if the identifier is null;
     */
    @Override
    public Optional<User> deleteElement(Long id) {
        throw new UnsupportedOperationException("This operation is not implemented yet!");
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
        throw new UnsupportedOperationException("This operation is not implemented yet!");
    }
}

package Repository.ConcreteRepositories.DB;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import Domain.Objects.Friendship;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.PageableValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.PagingRepository;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import util.datatypes.OrderedPair;
import util.paging.Page;
import util.paging.Pageable;

import java.security.InvalidParameterException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FriendRequestDBRepository implements PagingRepository<Long, FriendRequest> {
    private final String url, user, password;
    private final Validator<FriendRequest> friendRequestValidator;
    private final Validator<Pageable> pageableValidator;
    private final Repository<Long, User> userRepository;

    public FriendRequestDBRepository(Repository<Long, User> userRepository, Validator<FriendRequest> friendRequestValidator, String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
        this.friendRequestValidator = friendRequestValidator;
        this.userRepository = userRepository;
        this.pageableValidator = new PageableValidator();
    }

    private FriendRequest getTopFriendRequest(ResultSet resultSet){
        try {
            Long id = resultSet.getLong("id");
            Long from = resultSet.getLong("from_id");
            Long to = resultSet.getLong("to_id");
            FriendRequestStates status = FriendRequestStates.valueOf(resultSet.getString("status"));
            LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
            return new FriendRequest(id,from,to,status,dateTime);
        }
        catch (SQLException e){
            throw new RepositoryException("The column names couldn't be found!",e.getCause());
        }
    }
    /**
     * This function returns the wanted page based on the data from the pageable object(page number and page size)
     *
     * @param pageable an object which remembers the page count and the page size;
     * @return A page object, which contains the objects from the repository that are in the desired page.
     */

    @Override
    public Page<FriendRequest> findAllOnPage(Pageable pageable) {
        if(pageable == null){
            throw new RepositoryException("The pageable object can't be null!",new InvalidParameterException());
        }
        pageableValidator.validate(pageable);
        String sql = "SELECT * FROM friend_requests ORDER BY id LIMIT ? OFFSET ?";
        String sql2 = "SELECT COUNT(*) FROM friend_requests";
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,pageable.getPageSize());
            preparedStatement.setInt(2,pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<FriendRequest> friendRequests = new ArrayList<>();

            while(resultSet.next()){
                friendRequests.add(getTopFriendRequest(resultSet));
            }

            PreparedStatement countStatement = connection.prepareStatement(sql2);
            ResultSet resultSetCount = countStatement.executeQuery();
            int totalCount = 0;
            if(resultSetCount.next()){
                totalCount = resultSetCount.getInt(1);
            }
            return new Page<>(friendRequests,totalCount);
        }
        catch (SQLException e){
            throw new RepositoryException("The connection to the database couldn't be formed!",e.getCause());
        }
    }

    /**
     * This function returns the number of entities in the repository.
     *
     * @return the number of stored entities;
     */
    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement statement = connection.prepareStatement("SELECT count(*) AS count FROM friend_requests;");
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
     * This function returns a list of the entities in the repository that are accepted by the filter.
     *
     * @param filter a predicate which acts as a filter for the entities;
     * @return A list of the filtered entities in the repository.
     */
    @Override
    public List<FriendRequest> findAll(Predicate<FriendRequest> filter) {
        if(filter == null){
            throw new RepositoryException("The filter can't be null!",new IllegalArgumentException());
        }
        String sqlFindAll = "SELECT * FROM friend_requests ORDER BY id";
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement statement = connection.prepareStatement(sqlFindAll);
            ResultSet resultSet = statement.executeQuery();
            List<FriendRequest> friendRequests = new ArrayList<>();
            while(resultSet.next()){
                FriendRequest newFriendRequest = getTopFriendRequest(resultSet);
                if(filter.test(newFriendRequest)) {
                    friendRequests.add(newFriendRequest);
                }
            }
            return friendRequests;
        }
        catch (SQLException exception){
            throw new RepositoryException("The size script didn't work!",exception.getCause());
        }
    }

    /**
     * This function finds an entity by its identifier.
     *
     * @param id the identifier of the entity to find;
     * @return the entity with the specified identifier, or Optional.empty() if it doesn't exist in the repository;
     * @throws RepositoryException if the ID is null;
     */
    @Override
    public Optional<FriendRequest> findElement(Long id) {
        if(id == null){
            throw new RepositoryException("The id can't be null!",new IllegalArgumentException());
        }
        String findSql = "SELECT * FROM friend_requests WHERE id=?";
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement(findSql);
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return Optional.of(getTopFriendRequest(resultSet));
            }
            return Optional.empty();
        }
        catch (SQLException e){
            throw new RepositoryException("The connection couldn't be formed!",e.getCause());
        }
    }

    /**
     * This function validates and adds an entity to the repository.
     *
     * @param new_element the entity to add;
     * @return Optional.empty() if an entity with the same identifier already exists, and it returns the entity if the add operation is successful;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     * @throws RepositoryException if the entity is null, or its ID is null;
     */
    @Override
    public Optional<FriendRequest> addElement(FriendRequest new_element) {
        if(new_element == null){
            throw new RepositoryException("The new friend request can't be null!",new IllegalArgumentException());
        }
        if(userRepository.findElement(new_element.getFrom()).isEmpty() || userRepository.findElement(new_element.getTo()).isEmpty()){
            throw new RepositoryException("The new friend request can't be formed, as one of the users doesn't exist!",new IllegalArgumentException());
        }
        if(findElement(new_element.getId()).isPresent()){
            return Optional.empty();
        }
        friendRequestValidator.validate(new_element);

        String findSql = "INSERT INTO friend_requests(from_id,to_id,status,date) VALUES (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement(findSql);
            statement.setLong(1,new_element.getFrom());
            statement.setLong(2,new_element.getTo());
            statement.setString(3,new_element.getStatus().toString());
            statement.setTimestamp(4,Timestamp.valueOf(new_element.getDate()));
            try {
                statement.executeUpdate();
                return Optional.of(new_element);
            }
            catch (SQLException e){
                throw new RepositoryException("The user ID's for this friendship don't exist!",e.getCause());
            }
        }
        catch (SQLException e){
            throw new RepositoryException("The connection couldn't be formed!",e.getCause());
        }
    }

    /**
     * This function removes an entity from the repository by its identifier.
     *
     * @param id the identifier of the entity to remove;
     * @return Optional.empty() if no entity with the specified identifier exists, or the deleted element if the identifier exists;
     */
    @Override
    public Optional<FriendRequest> deleteElement(Long id) {
        if(id == null){
            throw new RepositoryException("The ID for delete can't be null!",new InvalidParameterException());
        }
        Optional<FriendRequest> foundFriendRequest = findElement(id);
        if(foundFriendRequest.isEmpty()){
            return Optional.empty();
        }
        String deleteSql = "DELETE FROM friend_requests WHERE id=?";
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement(deleteSql);
            statement.setLong(1,id);
            try {
                statement.executeUpdate();
                return foundFriendRequest;
            }
            catch (SQLException e){
                throw new RepositoryException("The user ID's for this friendship don't exist!",e.getCause());
            }
        }
        catch (SQLException e){
            throw new RepositoryException("The connection couldn't be formed!",e.getCause());
        }
    }

    /**
     * This function validates and replaces an existing entity.
     *
     * @param new_element the entity containing the replacement data;
     * @return the given element if the element's id doesn't exist in the repository, and it returns Optional.empty() on success;
     * @throws RepositoryException if the entity is null;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     */
    @Override
    public Optional<FriendRequest> modifyElement(FriendRequest new_element) {
        if(new_element == null){
            throw new RepositoryException("The new friend request can't be null!",new IllegalArgumentException());
        }
        if(userRepository.findElement(new_element.getFrom()).isEmpty() || userRepository.findElement(new_element.getTo()).isEmpty()){
            throw new RepositoryException("The new friend request can't be formed, as one of the users doesn't exist!",new IllegalArgumentException());
        }
        friendRequestValidator.validate(new_element);
        if(findElement(new_element.getId()).isEmpty()){
            return Optional.of(new_element);
        }
        String updateSql = "UPDATE friend_requests SET from_id=?,to_id=?,status=?,date=? WHERE id=?";
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statement = connection.prepareStatement(updateSql);
            statement.setLong(1,new_element.getFrom());
            statement.setLong(2,new_element.getTo());
            statement.setString(3,new_element.getStatus().toString());
            statement.setTimestamp(4,Timestamp.valueOf(new_element.getDate()));
            statement.setLong(5,new_element.getId());
            try {
                statement.executeUpdate();
                return Optional.empty();
            }
            catch (SQLException e){
                throw new RepositoryException("The user ID's for this friendship don't exist!",e.getCause());
            }
        }
        catch (SQLException e){
            throw new RepositoryException("The connection couldn't be formed!",e.getCause());
        }
    }
}

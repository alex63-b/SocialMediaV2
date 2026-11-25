package RepositoryTests.Factories;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import Domain.Objects.Friendship;
import Domain.Validation.IntegrityValidation.FriendRequestValidator;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendshipValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;
import util.factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryFactoryTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
    private static final String config_path = "test_data/Resources/test_config.properties";
    private static final String db_config_path = "test_data/Resources/db_test.properties";
    @BeforeEach
    public void setUp(){
        reloadFile();
    }
    @AfterEach
    public void tearDown(){
        reloadFile();
    }
    private void reloadFile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(user_path));
            writer.write("1,John Doe,johndoe@gmail.com\n" +
                    "2,Phil Smith,philsmith@gmail.com\n" +
                    "3,Luke Scott,lukescott@gmail.com");
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(friendship_path));
            writer.write("1,3,2025-12-13T17:02:22\n3,2,2024-07-01T21:03:24");
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void reloadDB(){
        String url = "jdbc:postgresql://localhost:5432/SMTestDB";
        String user = "postgres";
        String password = "postgres";

        List<String> nameList = List.of("John Doe","Phil Smith","Luke Scott");
        List<String> firstNameList = List.of("johndoe@gmail.com","philsmith@gmail.com","lukescott@gmail.com");

        List<Long> id1List = List.of(1L,2L);
        List<Long> id2List = List.of(3L,3L);
        List<LocalDateTime> dateList = List.of(LocalDateTime.parse("2025-12-13T17:02:22"),LocalDateTime.parse("2024-07-01T21:03:24"));

        List<Long> friendRequestId1List = List.of(1L,1L,2L);
        List<Long> friendRequestId2List = List.of(2L,3L,3L);
        List<FriendRequestStates> friendRequestStates = List.of(FriendRequestStates.REJECTED,FriendRequestStates.APPROVED,FriendRequestStates.APPROVED);
        List<LocalDateTime> friendRequestDateList = List.of(LocalDateTime.parse("2024-07-13T15:02:22"),LocalDateTime.parse("2025-12-13T15:02:22"),LocalDateTime.parse("2024-07-01T19:03:24"));

        String insertSql = "INSERT INTO users(first_name,last_name) VALUES (?,?)";
        String friendshipSql = "INSERT INTO friendships(id1,id2,friendshipDate) VALUES (?,?,?)";
        String insertFriendRequestSql = "INSERT INTO friend_requests(from_id, to_id, status, date) VALUES (?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users");
            statementDelete.executeUpdate();
            PreparedStatement statementResetId = connection.prepareStatement("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statementResetId.executeUpdate();
            PreparedStatement statementDelete2 = connection.prepareStatement("DELETE FROM friendships");
            statementDelete2.executeUpdate();
            PreparedStatement statementDeleteFriendRequests = connection.prepareStatement("DELETE FROM friend_requests");
            statementDeleteFriendRequests.executeUpdate();
            PreparedStatement statementResetFriendRequestId = connection.prepareStatement("ALTER SEQUENCE friend_requests_id_seq RESTART WITH 1");
            statementResetFriendRequestId.executeUpdate();
            IntStream.range(0,3).forEach((i) -> {
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSql);
                    statement.setString(2, nameList.get(i));
                    statement.setString(1, firstNameList.get(i));
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });
            IntStream.range(0,2).forEach((i) -> {
                try {
                    PreparedStatement statementFriend = connection.prepareStatement(friendshipSql);
                    statementFriend.setLong(1, id1List.get(i));
                    statementFriend.setLong(2, id2List.get(i));
                    statementFriend.setTimestamp(3, Timestamp.valueOf(dateList.get(i)));
                    statementFriend.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });
            IntStream.range(0,3).forEach((i) -> {
                try{
                    PreparedStatement statementFriendRequest = connection.prepareStatement(insertFriendRequestSql);
                    statementFriendRequest.setLong(1,friendRequestId1List.get(i));
                    statementFriendRequest.setLong(2,friendRequestId2List.get(i));
                    statementFriendRequest.setString(3,friendRequestStates.get(i).toString());
                    statementFriendRequest.setTimestamp(4, Timestamp.valueOf(friendRequestDateList.get(i)));
                    statementFriendRequest.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });

        }
        catch (SQLException e){
            throw new RepositoryException("Something went wrong!",e);
        }
    }
    @Test
    void testCreateUserRepository() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(config_path));

        Validator<User> userValidator = new UserValidator();
        Repository<Long,User> fileRepo = factory.createUserRepository(userValidator);

        assertEquals(3,fileRepo.size());
        if(fileRepo.findElement(1L).isPresent() && fileRepo.findElement(2L).isPresent() && fileRepo.findElement(3L).isPresent()) {
            assertEquals("John Doe", fileRepo.findElement(1L).get().getName());
            assertEquals("Phil Smith", fileRepo.findElement(2L).get().getName());
            assertEquals("Luke Scott", fileRepo.findElement(3L).get().getName());
        }
        else{
            fail();
        }
    }

    @Test
    void testCreateFriendshipRepository() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(config_path));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        assertThrows(ConfigException.class,() -> factory.createFriendshipRepository(friendshipValidator));

        Repository<Long,User> userRepository = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendshipFileRepo = factory.createFriendshipRepository(friendshipValidator);

        assertEquals(2,friendshipFileRepo.size());

        if(friendshipFileRepo.findElement(new OrderedPair<>(1L,3L)).isPresent() && friendshipFileRepo.findElement(new OrderedPair<>(3L,2L)).isPresent()) {
            assertEquals("John Doe", friendshipFileRepo.findElement(new OrderedPair<>(1L, 3L)).get().getFriend1().getName());
            assertEquals("Luke Scott", friendshipFileRepo.findElement(new OrderedPair<>(3L, 2L)).get().getFriend2().getName());
        }
        else{
            fail();
        }

        userRepository.deleteElement(1L);

        assertEquals(1,friendshipFileRepo.size());
    }

    @Test
    void testCreateFriendRequestRepository(){
        reloadDB();

        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(db_config_path));

        Validator<User> userValidator = new UserValidator();
        Validator<FriendRequest> friendRequestValidator = new FriendRequestValidator();

        assertThrows(ConfigException.class,() -> factory.createFriendRequestRepository(friendRequestValidator));

        Repository<Long,User> userRepository = factory.createUserRepository(userValidator);
        Repository<Long,FriendRequest> friendRequestRepository = factory.createFriendRequestRepository(friendRequestValidator);

        assertEquals(friendRequestRepository.size(),3);

        List<FriendRequest> allFriendRequests = friendRequestRepository.findAll(DefaultFilters::filterTrue);

        assertEquals(1L,allFriendRequests.getFirst().getFrom());
        assertEquals(2L,allFriendRequests.getFirst().getTo());

        assertEquals(1L,allFriendRequests.get(1).getFrom());
        assertEquals(3L,allFriendRequests.get(1).getTo());

        assertEquals(2L,allFriendRequests.getLast().getFrom());
        assertEquals(3L,allFriendRequests.getLast().getTo());

        assertEquals(FriendRequestStates.REJECTED,allFriendRequests.getFirst().getStatus());
        assertEquals(FriendRequestStates.APPROVED,allFriendRequests.get(1).getStatus());
        assertEquals(FriendRequestStates.APPROVED,allFriendRequests.getLast().getStatus());

        assertEquals(LocalDateTime.parse("2024-07-13T15:02:22"),allFriendRequests.getFirst().getDate());
    }
}
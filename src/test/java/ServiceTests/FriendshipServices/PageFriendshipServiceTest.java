package ServiceTests.FriendshipServices;

import Domain.Objects.Friendship;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.RepositoryFactory;
import Service.Factories.ServiceFactory;
import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.PageFriendshipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.datatypes.OrderedPair;
import util.factory.PropertyFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PageFriendshipServiceTest {
    private final static String url = "jdbc:postgresql://localhost:5432/SMTestDB";
    private final static String user = "postgres";
    private final static String password = "postgres";
    @BeforeEach
    public void setUp(){
        reloadDBData();
    }
    @AfterEach
    public void tearDown(){
        reloadDBData();
    }
    private void reloadDBData(){
        List<String> nameList = List.of("John Doe","Phil Smith","Luke Scott");
        List<String> firstNameList = List.of("johndoe@gmail.com","philsmith@gmail.com","lukescott@gmail.com");
        List<Long> id1List = List.of(1L,1L);
        List<Long> id2List = List.of(2L,3L);
        List<LocalDateTime> dateList = List.of(LocalDateTime.parse("2025-12-13T17:02:22"),LocalDateTime.parse("2024-07-01T21:03:24"));
        String insertSql = "INSERT INTO users(first_name,last_name) VALUES (?,?)";
        String friendshipSql = "INSERT INTO friendships(id1,id2,friendshipDate) VALUES (?,?,?)";

        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users");
            statementDelete.executeUpdate();
            PreparedStatement statementResetId = connection.prepareStatement("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statementResetId.executeUpdate();
            PreparedStatement statementDelete2 = connection.prepareStatement("DELETE FROM friendships");
            statementDelete2.executeUpdate();
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
        }
        catch (SQLException e){
            throw new RepositoryException("Something went wrong!",e);
        }
    }
    @Test
    void findOnPage() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        Properties properties = propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties");
        RepositoryFactory factory = new RepositoryFactory(properties);

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ServiceFactory serviceFactory = new ServiceFactory(properties);
        BasicFriendshipService service = serviceFactory.createFriendshipService(friendship_repo,user_repo);

        assertInstanceOf(PageFriendshipService.class,service);

        PageFriendshipService friendService = (PageFriendshipService) service;

        assertThrows(IllegalArgumentException.class,() -> friendService.findOnPage(-3));

        List<Friendship> page1 = friendService.findOnPage(0);
        assertEquals(2,page1.size());

        assertEquals("John Doe",page1.getFirst().getFriend1().getName());
        assertEquals("Phil Smith",page1.getFirst().getFriend2().getName());

        assertEquals("John Doe",page1.getLast().getFriend1().getName());
        assertEquals("Luke Scott",page1.getLast().getFriend2().getName());

        List<Friendship> page2 = friendService.findOnPage(1);
        assertEquals(0,page2.size());
    }

    @Test
    void getUserFriendships() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        Properties properties = propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties");
        RepositoryFactory factory = new RepositoryFactory(properties);

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ServiceFactory serviceFactory = new ServiceFactory(properties);
        BasicFriendshipService service = serviceFactory.createFriendshipService(friendship_repo,user_repo);

        assertInstanceOf(PageFriendshipService.class,service);

        PageFriendshipService friendService = (PageFriendshipService) service;

        assertThrows(IllegalArgumentException.class,() -> friendService.getUserFriendships(1L,-1));

        List<User> page1 = friendService.getUserFriendships(1L,0);
        assertEquals(2,page1.size());
        assertEquals("Phil Smith",page1.getFirst().getName());
        assertEquals("Luke Scott",page1.getLast().getName());

        List<User> page2 = friendService.getUserFriendships(2L,0);
        assertEquals(1,page2.size());
        assertEquals("John Doe",page2.getFirst().getName());

        assertThrows(RepositoryException.class,() -> friendService.getUserFriendships(-1L,0));
    }
}
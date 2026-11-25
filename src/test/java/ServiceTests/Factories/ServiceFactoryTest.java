package ServiceTests.Factories;

import Domain.Objects.Friendship;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendshipValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.RepositoryFactory;
import Service.Factories.ServiceFactory;
import Service.FriendshipServices.BasicFriendshipService;
import Service.FriendshipServices.ConsoleFriendshipService;
import Service.FriendshipServices.PageFriendshipService;
import Service.UserServices.BasicUserService;
import Service.UserServices.ConsoleUserService;
import Service.UserServices.PageUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.factory.PropertyFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ServiceFactoryTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
    private static final String config_path = "test_data/Resources/test_config.properties";
    private static final String db_config_path = "test_data/Resources/db_test.properties";

    private final static String url = "jdbc:postgresql://localhost:5432/SMTestDB";
    private final static String user = "postgres";
    private final static String password = "postgres";
    @BeforeEach
    public void setUp(){
        reloadFile();
        reloadDB();
    }
    @AfterEach
    public void tearDown(){
        reloadFile();
        reloadDB();
    }
    private void reloadFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(user_path));
            writer.write("1,John Doe,Macaroni\n" +
                    "2,Phil Smith,Coulson\n" +
                    "3,Luke Scott,Smithson");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(friendship_path));
            writer.write("1,3,2025-12-13T17:02:22\n3,2,2024-07-01T21:03:24");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void reloadDB(){
        List<String> nameList = List.of("John Doe","Phil Smith","Luke Scott");
        List<String> firstNameList = List.of("johndoe@gmail.com","philsmith@gmail.com","lukescott@gmail.com");
        List<Long> id1List = List.of(1L,2L);
        List<Long> id2List = List.of(3L,3L);
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
    void createUserService() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        Properties properties = propertyFactory.propertiesFromConfigFile(config_path);
        RepositoryFactory factory = new RepositoryFactory(properties);

        Validator<User> userValidator = new UserValidator();
        Repository<Long,User> fileRepo = factory.createUserRepository(userValidator);

        ServiceFactory serviceFactory = new ServiceFactory(properties);
        BasicUserService userService = serviceFactory.createUserService(fileRepo);

        assertEquals(ConsoleUserService.class, userService.getClass());

        ConsoleUserService consoleUserService = (ConsoleUserService) userService;

        assertEquals("1. The user John Doe Macaroni;\n2. The user Phil Smith Coulson;\n3. The user Luke Scott Smithson;\n",consoleUserService.showAllUsers());

        Properties newProperties = propertyFactory.propertiesFromConfigFile(db_config_path);
        RepositoryFactory newFactory = new RepositoryFactory(newProperties);
        Repository<Long,User> dbRepo = newFactory.createUserRepository(userValidator);

        serviceFactory = new ServiceFactory(newProperties);
        BasicUserService userService2 = serviceFactory.createUserService(dbRepo);

        assertEquals(PageUserService.class, userService2.getClass());
        assertEquals(3, userService2.searchByName("").size());
    }

    @Test
    void createFriendshipService() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        Properties properties = propertyFactory.propertiesFromConfigFile(config_path);
        RepositoryFactory factory = new RepositoryFactory(properties);

        Validator<User> userValidator = new UserValidator();
        Repository<Long,User> userFileRepo = factory.createUserRepository(userValidator);

        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        Repository<OrderedPair<Long>,Friendship> fileRepo = factory.createFriendshipRepository(friendshipValidator);

        ServiceFactory serviceFactory = new ServiceFactory(properties);
        BasicFriendshipService friendshipService = serviceFactory.createFriendshipService(fileRepo,userFileRepo);

        assertEquals(ConsoleFriendshipService.class, friendshipService.getClass());

        ConsoleFriendshipService consoleFriendshipService = (ConsoleFriendshipService) friendshipService;

        assertEquals("Friendship between Phil Smith(ID 2) and Luke Scott(ID 3) started on the date 1 JULY 2024, at 21:03:24;\n" +
                "Friendship between John Doe(ID 1) and Luke Scott(ID 3) started on the date 13 DECEMBER 2025, at 17:02:22;\n",consoleFriendshipService.showAllFriendships());

        Properties newProperties = propertyFactory.propertiesFromConfigFile(db_config_path);
        RepositoryFactory newFactory = new RepositoryFactory(newProperties);

        Repository<Long,User> userDbRepo = newFactory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendshipDbRepo = newFactory.createFriendshipRepository(friendshipValidator);

        serviceFactory = new ServiceFactory(newProperties);
        BasicFriendshipService friendshipService2 = serviceFactory.createFriendshipService(friendshipDbRepo,userDbRepo);

        assertEquals(PageFriendshipService.class, friendshipService2.getClass());

        PageFriendshipService service2 = (PageFriendshipService) friendshipService2;

        assertEquals(1,service2.communityCount());
    }
}
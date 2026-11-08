package Tests.RepositoryTests.ConcreteTests.DB;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import Repository.Helpers.DefaultFilters;
import Tests.RepositoryTests.ConcreteTests.ConcreteRepoTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FriendshipDBTest {
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
    public void testLoad(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        if(friendship_repo.findElement(new Pair<>(1L,3L)).isPresent()) {
            assertEquals("John Doe", friendship_repo.findElement(new Pair<>(1L, 3L)).get().getFriend1().getName());
            assertEquals("Luke Scott", friendship_repo.findElement(new Pair<>(1L, 3L)).get().getFriend2().getName());
        }
        else{
            fail();
        }
        if(friendship_repo.findElement(new Pair<>(3L,2L)).isPresent()) {
            assertEquals("Luke Scott", friendship_repo.findElement(new Pair<>(3L, 2L)).get().getFriend2().getName());
            assertEquals("Phil Smith", friendship_repo.findElement(new Pair<>(3L, 2L)).get().getFriend1().getName());
        }
        else{
            fail();
        }
    }
    @Test
    public void testStore(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        if(user_repo.findElement(1L).isPresent() && user_repo.findElement(2L).isPresent()) {
            friendship_repo.addElement(new Friendship(user_repo.findElement(1L).get(), user_repo.findElement(2L).get(), LocalDateTime.now()));
        }
        else{
            fail();
        }

        Repository<Pair<Long>,Friendship> friendship_repo_2 = factory.createFriendshipRepository(friendshipValidator);

        int length = 0;
        for(Friendship f : friendship_repo_2.findAll(DefaultFilters::filterTrue)){
            length += 1;
            assertEquals(Optional.of(f),friendship_repo.findElement(f.getId()));
        }
        assertEquals(3,length);
    }
    @Test
    public void testCascadeDelete(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.cascadeDelete(user_repo,friendship_repo);
    }

    @Test
    public void testCascadeModify(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.cascadeModify(user_repo,friendship_repo);
    }

    @Test
    public void testErrorAdd(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.errorAdd(user_repo,friendship_repo);
    }
}

package RepositoryTests.ConcreteTests.DB;

import Domain.Objects.Friendship;
import Repository.ConcreteRepositories.DB.FriendshipDBRepository;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendshipValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import util.factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import Repository.Helpers.DefaultFilters;
import RepositoryTests.ConcreteTests.ConcreteRepoTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

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
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        if(friendship_repo.findElement(new OrderedPair<>(1L,3L)).isPresent()) {
            assertEquals("John Doe", friendship_repo.findElement(new OrderedPair<>(1L, 3L)).get().getFriend1().getName());
            assertEquals("Luke Scott", friendship_repo.findElement(new OrderedPair<>(1L, 3L)).get().getFriend2().getName());
        }
        else{
            fail();
        }
        if(friendship_repo.findElement(new OrderedPair<>(3L,2L)).isPresent()) {
            assertEquals("Luke Scott", friendship_repo.findElement(new OrderedPair<>(3L, 2L)).get().getFriend2().getName());
            assertEquals("Phil Smith", friendship_repo.findElement(new OrderedPair<>(3L, 2L)).get().getFriend1().getName());
        }
        else{
            fail();
        }
    }
    @Test
    public void testStore(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        if(user_repo.findElement(1L).isPresent() && user_repo.findElement(2L).isPresent()) {
            friendship_repo.addElement(new Friendship(user_repo.findElement(1L).get(), user_repo.findElement(2L).get(), LocalDateTime.now()));
        }
        else{
            fail();
        }

        Repository<OrderedPair<Long>,Friendship> friendship_repo_2 = factory.createFriendshipRepository(friendshipValidator);

        int length = 0;
        for(Friendship f : friendship_repo_2.findAll(DefaultFilters::filterTrue)){
            length += 1;
            assertEquals(Optional.of(f),friendship_repo.findElement(f.getId()));
        }
        assertEquals(3,length);
    }
    @Test
    public void testCascadeDelete(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.cascadeDelete(user_repo,friendship_repo);
    }

    @Test
    public void testCascadeModify(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.cascadeModify(user_repo,friendship_repo);
    }

    @Test
    public void testErrorAdd(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.errorAdd(user_repo,friendship_repo);
    }

    @Test
    public void testFindAllOnPage(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        assertInstanceOf(FriendshipDBRepository.class,friendship_repo);

        FriendshipDBRepository friendshipRepo = (FriendshipDBRepository) friendship_repo;

        assertThrows(RepositoryException.class,() -> friendshipRepo.findAllOnPage(null));
        assertThrows(IllegalArgumentException.class,() -> friendshipRepo.findAllOnPage(new Pageable(0,0)));

        Page<Friendship> firstPage = friendshipRepo.findAllOnPage(new Pageable(0,2));
        assertEquals(2,firstPage.getTotalNumberOfElements());

        List<Friendship> friendshipList1 = (List<Friendship>)firstPage.getElementsOnPage();
        assertEquals(2,friendshipList1.size());

        assertEquals("John Doe",friendshipList1.getFirst().getFriend1().getName());
        assertEquals("Luke Scott",friendshipList1.getFirst().getFriend2().getName());

        assertEquals("Phil Smith",friendshipList1.getLast().getFriend1().getName());
        assertEquals("Luke Scott",friendshipList1.getLast().getFriend2().getName());

        Page<Friendship> secondPage = friendshipRepo.findAllOnPage(new Pageable(1,2));
        List<Friendship> friendshipList2 = (List<Friendship>)secondPage.getElementsOnPage();
        assertEquals(0,friendshipList2.size());

        Page<Friendship> page3 = friendshipRepo.findAllOnPage(new Pageable(0,121));
        List<Friendship> friendshipList3 = (List<Friendship>)page3.getElementsOnPage();
        assertEquals(2,friendshipList3.size());

        assertEquals("John Doe",friendshipList3.getFirst().getFriend1().getName());
        assertEquals("Luke Scott",friendshipList3.getFirst().getFriend2().getName());

        assertEquals("Phil Smith",friendshipList3.getLast().getFriend1().getName());
        assertEquals("Luke Scott",friendshipList3.getLast().getFriend2().getName());
    }

    @Test
    public void testFindFriendsOnPage(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        assertInstanceOf(FriendshipDBRepository.class,friendship_repo);

        FriendshipDBRepository friendshipRepo = (FriendshipDBRepository) friendship_repo;

        assertThrows(RepositoryException.class,() -> friendshipRepo.findFriendsOnPage(4L,new Pageable(1,2)));
        assertThrows(RepositoryException.class,() -> friendshipRepo.findFriendsOnPage(1L,null));
        assertThrows(RepositoryException.class,() -> friendshipRepo.findFriendsOnPage(null,new Pageable(1,2)));
        assertThrows(IllegalArgumentException.class,() -> friendshipRepo.findFriendsOnPage(1L,new Pageable(-1,2)));

        Page<User> page1 = friendshipRepo.findFriendsOnPage(3L,new Pageable(0,1));
        assertEquals(2,page1.getTotalNumberOfElements());

        List<User> friends1 = (List<User>)page1.getElementsOnPage();
        assertEquals(1,friends1.size());
        assertEquals("John Doe",friends1.getFirst().getName());

        Page<User> page2 = friendshipRepo.findFriendsOnPage(1L,new Pageable(2,1));
        List<User> friends2 = (List<User>)page2.getElementsOnPage();
        assertEquals(0,friends2.size());

        Page<User> page3 = friendshipRepo.findFriendsOnPage(2L,new Pageable(0,16));
        List<User> friends3 = (List<User>)page3.getElementsOnPage();
        assertEquals(1,friends3.size());

        assertEquals("Luke Scott",friends3.getFirst().getName());
    }
}

package RepositoryTests.ConcreteTests.DB;

import Domain.Objects.User;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.DB.UserDBRepository;
import Repository.Exceptions.RepositoryException;
import util.factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import Repository.Helpers.DefaultFilters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class UserDBTest {
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
        String insertSql = "INSERT INTO users(first_name,last_name) VALUES (?,?)";

        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users");
            statementDelete.executeUpdate();
            PreparedStatement statementResetId = connection.prepareStatement("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statementResetId.executeUpdate();
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
        }
        catch (SQLException e){
            throw new RepositoryException("Something went wrong!",e);
        }
    }
    @Test
    void testLoad() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        assertEquals(3,new_repo.size());
        if(new_repo.findElement(1L).isPresent() && new_repo.findElement(2L).isPresent() && new_repo.findElement(3L).isPresent()) {
            assertEquals("John Doe", new_repo.findElement(1L).get().getName());
            assertEquals("philsmith@gmail.com", new_repo.findElement(2L).get().getFirstName());
            assertEquals("Luke Scott", new_repo.findElement(3L).get().getName());
        }
        else{
            fail();
        }
    }

    @Test
    void testStore() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        new_repo.addElement(new User(4L,"Mike Forge","mikeforge@gmail.com"));

        Repository<Long,User> test_repo = factory.createUserRepository(validator);
        assertEquals(4,test_repo.size());
        for(User u : test_repo.findAll(DefaultFilters::filterTrue)){
            assertEquals(Optional.of(u),new_repo.findElement(u.getId()));
        }
    }
    @Test
    void testFindAll(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        List<User> fullRepo = new_repo.findAll(DefaultFilters::filterTrue);

        assertEquals(3,fullRepo.size());
        assertEquals("John Doe",fullRepo.getFirst().getName());
        assertEquals("Phil Smith",fullRepo.get(1).getName());
        assertEquals("Luke Scott",fullRepo.getLast().getName());

        List<User> filterRepo = new_repo.findAll((u) -> u.getName().contains("P"));

        assertEquals(1,filterRepo.size());
        assertEquals("Phil Smith",filterRepo.getFirst().getName());

        assertThrows(RepositoryException.class,() -> new_repo.findElement(null));
    }

    @Test
    void testAdd(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        User right_user = new User(4L,"Mike","Forge");

        assertEquals(Optional.of(right_user),new_repo.addElement(right_user));
        assertEquals(4,new_repo.size());

        if(new_repo.findElement(4L).isPresent()) {
            assertEquals(right_user, new_repo.findElement(4L).get());
        }
        else{
            fail();
        }

        User wrong_user = new User(9L,"John Doe","");

        assertThrows(ValidationException.class,() -> new_repo.addElement(wrong_user));
        assertThrows(RepositoryException.class,() -> new_repo.addElement(null));
        assertFalse(new_repo.addElement(right_user).isPresent());
    }

    @Test
    void testDelete(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        Optional<User> test_user = new_repo.findElement(2L);

        assertFalse(new_repo.deleteElement(5L).isPresent());

        assertEquals(test_user,new_repo.deleteElement(2L));

        assertEquals(2,new_repo.size());
        assertFalse(new_repo.findElement(2L).isPresent());
    }

    @Test
    void testModify(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        User new_user = new User(1L,"Mike","Loft");
        User wrong_user = new User(7L,"Mike","Loft");

        assertThrows(RepositoryException.class,() -> new_repo.modifyElement(null));
        assertThrows(ValidationException.class,() -> new_repo.modifyElement(new User(-2L,"a","b")));

        assertTrue(new_repo.modifyElement(new_user).isEmpty());
        assertEquals(Optional.of(wrong_user),new_repo.modifyElement(wrong_user));
        if(new_repo.findElement(new_user.getId()).isPresent()) {
            assertEquals("Mike",new_repo.findElement(new_user.getId()).get().getName());
            assertEquals("Loft",new_repo.findElement(new_user.getId()).get().getFirstName());
        }
        else{
            fail();
        }
    }
    @Test
    void testFindAllOnPage(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        assertInstanceOf(UserDBRepository.class, new_repo);

        UserDBRepository userRepo = (UserDBRepository) new_repo;

        assertThrows(RepositoryException.class, () -> userRepo.findAllOnPage(null));
        assertThrows(IllegalArgumentException.class, () -> userRepo.findAllOnPage(new Pageable(-1,-2)));

        Page<User> firstPage = userRepo.findAllOnPage(new Pageable(0,2));
        assertEquals(3, firstPage.getTotalNumberOfElements());

        List<User> users = (List<User>) firstPage.getElementsOnPage();
        assertEquals(2, users.size());

        assertEquals("John Doe",users.getFirst().getName());
        assertEquals("Phil Smith",users.getLast().getName());

        Page<User> secondPage = userRepo.findAllOnPage(new Pageable(1,2));

        List<User> users2 = (List<User>) secondPage.getElementsOnPage();
        assertEquals(1, users2.size());

        assertEquals("Luke Scott",users2.getFirst().getName());

        Page<User> thirdPage = userRepo.findAllOnPage(new Pageable(3,1));

        List<User> users3 = (List<User>) thirdPage.getElementsOnPage();
        assertEquals(0,users3.size());

        Page<User> testPage = userRepo.findAllOnPage(new Pageable(1,1));

        List<User> users4 = (List<User>) testPage.getElementsOnPage();

        assertEquals(1,users4.size());
        assertEquals("Phil Smith",users4.getFirst().getName());
    }
}

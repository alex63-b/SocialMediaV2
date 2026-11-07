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
import Repository.Factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import Repository.Helpers.DefaultFilters;
import Tests.RepositoryTests.ConcreteTests.ConcreteRepoTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FriendshipDBTest {
    @BeforeEach
    public void setUp(){
        reloadDBData();
    }
    @AfterEach
    public void tearDown(){
        reloadDBData();
    }
    private void reloadDBData(){

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
            assertEquals("Luke Scott", friendship_repo.findElement(new Pair<>(3L, 2L)).get().getFriend1().getName());
            assertEquals("Phil Smith", friendship_repo.findElement(new Pair<>(3L, 2L)).get().getFriend2().getName());
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
    @Test
    public void testDoubleFriendAdd(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.doubleFriendAdd(user_repo,friendship_repo);
    }
    @Test
    public void testFriendshipDelete(){
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties"));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendship_repo = factory.createFriendshipRepository(friendshipValidator);

        ConcreteRepoTestHelper.friendshipDelete(user_repo,friendship_repo);
    }
}

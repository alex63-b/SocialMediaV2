package Tests.RepositoryTests.Factories;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.ConfigException;
import Repository.Factory.PropertyFactory;
import Repository.Factory.RepositoryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryFactoryTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
    private static final String config_path = "test_data/Resources/test_config.properties";
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
    @Test
    void testCreateUserRepository() {
        PropertyFactory propertyFactory = new PropertyFactory();
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
        PropertyFactory propertyFactory = new PropertyFactory();
        RepositoryFactory factory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(config_path));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        assertThrows(ConfigException.class,() -> factory.createFriendshipRepository(friendshipValidator));

        Repository<Long,User> userRepository = factory.createUserRepository(userValidator);
        Repository<Pair<Long>,Friendship> friendshipFileRepo = factory.createFriendshipRepository(friendshipValidator);

        assertEquals(2,friendshipFileRepo.size());

        if(friendshipFileRepo.findElement(new Pair<>(1L,3L)).isPresent() && friendshipFileRepo.findElement(new Pair<>(3L,2L)).isPresent()) {
            assertEquals("John Doe", friendshipFileRepo.findElement(new Pair<>(1L, 3L)).get().getFriend1().getName());
            assertEquals("Luke Scott", friendshipFileRepo.findElement(new Pair<>(3L, 2L)).get().getFriend1().getName());
        }
        else{
            fail();
        }

        userRepository.deleteElement(1L);

        assertEquals(1,friendshipFileRepo.size());
    }
}
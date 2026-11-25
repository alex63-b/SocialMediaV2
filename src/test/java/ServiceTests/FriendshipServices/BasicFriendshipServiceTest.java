package ServiceTests.FriendshipServices;

import Domain.Objects.Friendship;
import Repository.Factory.RepositoryFactory;
import Service.Factories.ServiceFactory;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.FriendshipValidator;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.File.UserFileRepository;
import Repository.ConcreteRepositories.File.FriendshipFileRepository;
import Service.FriendshipServices.BasicFriendshipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.factory.PropertyFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BasicFriendshipServiceTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
    private static final String user_big_path = "test_data/Users/big_user_data.csv";
    private static final String friendship_big_path = "test_data/Friendships/big_friendship_data.csv";

    private static final String file_config_path = "test_data/Resources/test_config.properties";
    @BeforeEach
    void setUp() {
        reloadFile();
    }

    @AfterEach
    void tearDown() {
        reloadFile();
    }
    private void reloadFile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("test_data/Friendships/friendship_data.csv"));
            writer.write("1,3,2025-12-13T17:02:22\n3,2,2024-07-01T21:03:24");
            writer.close();
            writer = new BufferedWriter(new FileWriter("test_data/Users/user_data.csv"));
            writer.write("1,John Doe,johndoe@gmail.com\n" +
                    "2,Phil Smith,philsmith@gmail.com\n" +
                    "3,Luke Scott,lukescott@gmail.com");
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testAddFriendship() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertThrows(ValidationException.class,() -> service.addFriendship(1L,3L));
        assertThrows(ValidationException.class,() -> service.addFriendship(3L,1L));
        assertThrows(ValidationException.class,() -> service.addFriendship(-1L,-3L));

        service.addFriendship(1L,2L);

        assertEquals(3,friendship_repo.size());
        if(friendship_repo.findElement(new OrderedPair<>(1L,2L)).isPresent()) {
            assertEquals(1L, friendship_repo.findElement(new OrderedPair<>(1L, 2L)).get().getFriend1().getId());
            assertEquals(2L, friendship_repo.findElement(new OrderedPair<>(1L, 2L)).get().getFriend2().getId());
            LocalDateTime date = LocalDateTime.now().minusMinutes(1);
            assertTrue(date.isBefore(friendship_repo.findElement(new OrderedPair<>(1L, 2L)).get().getDate()));
            assertTrue(LocalDateTime.now().isAfter(friendship_repo.findElement(new OrderedPair<>(1L, 2L)).get().getDate()));
        }
        else{
            fail();
        }
    }
    @Test
    void testUpdateFriendship(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        RepositoryFactory repositoryFactory = new RepositoryFactory(propertyFactory.propertiesFromConfigFile(file_config_path));

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long,User> user_repo = repositoryFactory.createUserRepository(userValidator);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = repositoryFactory.createFriendshipRepository(friendshipValidator);

        ServiceFactory serviceFactory = new ServiceFactory(propertyFactory.propertiesFromConfigFile(file_config_path));
        BasicFriendshipService service = serviceFactory.createFriendshipService(friendship_repo,user_repo);

        assertThrows(ValidationException.class,() -> service.updateFriendship(1L,2L,"2026-07-13T21:03:24"));
        assertThrows(ValidationException.class,() -> service.updateFriendship(1L,2L,"22313"));

        service.updateFriendship(1L,3L,"2026-07-13T21:03:24");

        if(friendship_repo.findElement(new OrderedPair<>(1L,3L)).isPresent()){
            assertEquals("2026-07-13T21:03:24",friendship_repo.findElement(new OrderedPair<>(1L,3L)).get().getDate().toString());
        }
        else{
            fail();
        }
    }

    @Test
    void testDeleteFriendship() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertThrows(ValidationException.class,() -> service.deleteFriendship(9L,7L));

        service.deleteFriendship(1L,3L);

        assertEquals(1,friendship_repo.size());
        assertEquals(Optional.empty(),friendship_repo.findElement(new OrderedPair<>(1L,3L)));
    }

    @Test
    void testCommunityCount() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertEquals(1,service.communityCount());

        UserFileRepository user_repo_2 = new UserFileRepository(userValidator,user_big_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo_2 = new FriendshipFileRepository(user_repo_2,friendshipValidator,friendship_big_path);
        BasicFriendshipService service_2 = new BasicFriendshipService(friendship_repo_2,user_repo_2);

        assertEquals(4,service_2.communityCount());
    }
    @Test
    void getMostSociableCommunity(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        ArrayList<User> community = service.getMostSociableCommunity();

        assertEquals("John Doe",community.getFirst().getName());
        assertEquals("Phil Smith",community.get(1).getName());
        assertEquals("Luke Scott",community.getLast().getName());

        UserFileRepository user_repo_2 = new UserFileRepository(userValidator,user_big_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo_2 = new FriendshipFileRepository(user_repo_2,friendshipValidator,friendship_big_path);
        BasicFriendshipService service_2 = new BasicFriendshipService(friendship_repo_2,user_repo_2);

        ArrayList<User> community2 = service_2.getMostSociableCommunity();

        assertEquals("Phil Smith",community2.getFirst().getName());
        assertEquals("Luke Scott",community2.get(1).getName());
        assertEquals("John Smith",community2.get(2).getName());
        assertEquals("John Poppins",community2.get(3).getName());
        assertEquals("Phil Parker",community2.getLast().getName());
    }
    @Test
    void getUserFriendships(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<OrderedPair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        List<User> friends = service.getUserFriendships(3L);
        assertEquals(2,friends.size());

        assertEquals(1L,friends.getFirst().getId());
        assertEquals(2L,friends.getLast().getId());

        List<User> friends1 = service.getUserFriendships(1L);

        assertEquals(1,friends1.size());

        assertEquals(3L,friends1.getFirst().getId());
    }
}

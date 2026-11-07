package Tests.ServiceTests;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.Exceptions.RepositoryException;
import Service.FriendshipServices.BasicFriendshipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BasicFriendshipServiceTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
    private static final String user_big_path = "test_data/Users/big_user_data.csv";
    private static final String friendship_big_path = "test_data/Friendships/big_friendship_data.csv";
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
        Repository<Pair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertThrows(RepositoryException.class,() -> service.addFriendship(1L,3L));
        assertThrows(RepositoryException.class,() -> service.addFriendship(3L,1L));
        assertThrows(ValidationException.class,() -> service.addFriendship(-1L,-3L));

        service.addFriendship(1L,2L);

        assertEquals(3,friendship_repo.size());
        if(friendship_repo.findElement(new Pair<>(1L,2L)).isPresent()) {
            assertEquals(1L, friendship_repo.findElement(new Pair<>(1L, 2L)).get().getFriend1().getId());
            assertEquals(2L, friendship_repo.findElement(new Pair<>(1L, 2L)).get().getFriend2().getId());
            LocalDateTime date = LocalDateTime.now().minusMinutes(1);
            assertTrue(date.isBefore(friendship_repo.findElement(new Pair<>(1L, 2L)).get().getDate()));
            assertTrue(LocalDateTime.now().isAfter(friendship_repo.findElement(new Pair<>(1L, 2L)).get().getDate()));
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
        Repository<Pair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertThrows(ValidationException.class,() -> service.deleteFriendship(9L,7L));

        service.deleteFriendship(1L,3L);

        assertEquals(1,friendship_repo.size());
        assertEquals(Optional.empty(),friendship_repo.findElement(new Pair<>(1L,3L)));
    }

    @Test
    void testCommunityCount() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<Pair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        assertEquals(1,service.communityCount());

        UserFileRepository user_repo_2 = new UserFileRepository(userValidator,user_big_path);
        Repository<Pair<Long>,Friendship> friendship_repo_2 = new FriendshipFileRepository(user_repo_2,friendshipValidator,friendship_big_path);
        BasicFriendshipService service_2 = new BasicFriendshipService(friendship_repo_2,user_repo_2);

        assertEquals(4,service_2.communityCount());
    }
    @Test
    void getMostSociableCommunity(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<Pair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        BasicFriendshipService service = new BasicFriendshipService(friendship_repo,user_repo);

        ArrayList<User> community = service.getMostSociableCommunity();

        assertEquals("John Doe",community.getFirst().getName());
        assertEquals("Phil Smith",community.get(1).getName());
        assertEquals("Luke Scott",community.getLast().getName());

        UserFileRepository user_repo_2 = new UserFileRepository(userValidator,user_big_path);
        Repository<Pair<Long>,Friendship> friendship_repo_2 = new FriendshipFileRepository(user_repo_2,friendshipValidator,friendship_big_path);
        BasicFriendshipService service_2 = new BasicFriendshipService(friendship_repo_2,user_repo_2);

        ArrayList<User> community2 = service_2.getMostSociableCommunity();

        assertEquals("Phil Smith",community2.getFirst().getName());
        assertEquals("Luke Scott",community2.get(1).getName());
        assertEquals("John Smith",community2.get(2).getName());
        assertEquals("John Poppins",community2.get(3).getName());
        assertEquals("Phil Parker",community2.getLast().getName());
    }
}

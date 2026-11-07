package Tests.RepositoryTests.ConcreteTests.File;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;
import Tests.RepositoryTests.ConcreteTests.ConcreteRepoTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FriendshipFileRepositoryTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
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
            BufferedWriter writer = new BufferedWriter(new FileWriter("test_data/Friendships/friendship_data.csv"));
            writer.write("1,3,2025-12-13T17:02:22\n3,2,2024-07-01T21:03:24");
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("test_data/Users/user_data.csv"));
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
    public void testLoad(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);

        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_1.csv");
        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_2.csv");
        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_3.csv");
        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_4.csv");
        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_5.csv");
        new FriendshipFileRepository(user_repo,friendshipValidator,"test_data/Friendships/corrupted_friendship_6.csv");

        FriendshipFileRepository friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        Repository<Pair<Long>, Friendship> empty_repo = new FriendshipFileRepository(user_repo,friendshipValidator,"a");
        assertEquals(0,empty_repo.size());

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
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        if(user_repo.findElement(1L).isPresent() && user_repo.findElement(2L).isPresent()) {
            friendship_repo.addElement(new Friendship(user_repo.findElement(1L).get(), user_repo.findElement(2L).get(), LocalDateTime.now()));
        }
        else{
            fail();
        }

        FriendshipFileRepository friendship_repo_2 = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        int length = 0;
        for(Friendship f : friendship_repo_2.findAll(DefaultFilters::filterTrue)){
            length += 1;
            assertEquals(Optional.of(f),friendship_repo.findElement(f.getId()));
        }
        assertEquals(3,length);
    }
    @Test
    public void testCascadeDelete(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        user_repo.deleteElement(2L);

        assertEquals(1, friendship_repo.size());

        ConcreteRepoTestHelper.cascadeDelete(user_repo,friendship_repo);
    }

    @Test
    public void testCascadeModify(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        ConcreteRepoTestHelper.cascadeModify(user_repo,friendship_repo);
    }

    @Test
    public void testErrorAdd(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);

        ConcreteRepoTestHelper.errorAdd(user_repo,friendship_repo);
    }
    @Test
    public void testDoubleFriendAdd(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository userRepo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendshipRepo = new FriendshipFileRepository(userRepo,friendshipValidator,friendship_path);

        ConcreteRepoTestHelper.doubleFriendAdd(userRepo,friendshipRepo);
    }
    @Test
    public void testFriendshipDelete(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository userRepo = new UserFileRepository(userValidator,user_path);
        FriendshipFileRepository friendshipRepo = new FriendshipFileRepository(userRepo,friendshipValidator,friendship_path);

        ConcreteRepoTestHelper.friendshipDelete(userRepo,friendshipRepo);
    }
}

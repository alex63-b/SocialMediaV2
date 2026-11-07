package Tests.ServiceTests;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.UserFileRepository;
import Service.FriendshipServices.ConsoleFriendshipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleFriendshipServiceTest {
    private static final String user_path = "test_data/Users/user_data.csv";
    private static final String friendship_path = "test_data/Friendships/friendship_data.csv";
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
    void testShowAllFriendships() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        UserFileRepository user_repo = new UserFileRepository(userValidator,user_path);
        Repository<Pair<Long>,Friendship> friendship_repo = new FriendshipFileRepository(user_repo,friendshipValidator,friendship_path);
        ConsoleFriendshipService service = new ConsoleFriendshipService(friendship_repo,user_repo);

        assertEquals("Friendship between Luke Scott(ID 3) and Phil Smith(ID 2) started on the date 1 JULY 2024, at 21:03:24;\n" +
                        "Friendship between John Doe(ID 1) and Luke Scott(ID 3) started on the date 13 DECEMBER 2025, at 17:02:22;\n",service.showAllFriendships());
    }
}
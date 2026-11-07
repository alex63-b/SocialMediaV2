package Tests.RepositoryTests.ConcreteTests;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConcreteRepoTestHelper {
    public static void cascadeDelete(Repository<Long, User> userRepo, Repository<Pair<Long>, Friendship> friendshipRepo){
        userRepo.deleteElement(2L);
        assertEquals(1, friendshipRepo.size());

        for(Friendship f : friendshipRepo.findAll(DefaultFilters::filterTrue)){
            assertEquals(1L,f.getFriend1().getId());
            assertEquals(3L,f.getFriend2().getId());
        }
    }
    public static void cascadeModify(Repository<Long, User> userRepo, Repository<Pair<Long>, Friendship> friendshipRepo){
        if(userRepo.findElement(1L).isPresent() && userRepo.findElement(3L).isPresent()) {
            friendshipRepo.modifyElement(new Friendship(userRepo.findElement(1L).get(), userRepo.findElement(3L).get(), LocalDateTime.parse("2024-02-23T10:11:12")));
        }
        else{
            fail();
        }
        if(friendshipRepo.findElement(new Pair<>(1L,3L)).isPresent()) {
            assertEquals(LocalDateTime.parse("2024-02-23T10:11:12"), friendshipRepo.findElement(new Pair<>(1L, 3L)).get().getDate());
        }
        else{
            fail();
        }

        userRepo.modifyElement(new User(2L,"Thor Odinson","thor@odinson.com"));
        if(friendshipRepo.findElement(new Pair<>(3L,2L)).isPresent()) {
            assertEquals("Thor Odinson", friendshipRepo.findElement(new Pair<>(3L, 2L)).get().getFriend2().getName());
        }
        else{
            fail();
        }
    }
    public static void errorAdd(Repository<Long, User> userRepo, Repository<Pair<Long>, Friendship> friendshipRepo){
        if(userRepo.findElement(1L).isPresent() && userRepo.findElement(3L).isPresent()) {
            User user1 = userRepo.findElement(1L).get();
            User user3 = userRepo.findElement(3L).get();
            assertThrows(RepositoryException.class, () -> friendshipRepo.addElement(new Friendship(user1, user3, LocalDateTime.parse("2024-02-23T10:11:12"))));
            assertThrows(RepositoryException.class, () -> friendshipRepo.addElement(new Friendship(user3, user1, LocalDateTime.parse("2024-02-23T10:11:12"))));
            assertThrows(ValidationException.class, () -> friendshipRepo.addElement(new Friendship(user1, user1, LocalDateTime.parse("2024-02-23T10:11:12"))));

            assertThrows(RepositoryException.class, () -> friendshipRepo.addElement(new Friendship(user3, new User(9L, "a", "b"), LocalDateTime.parse("2024-02-23T10:11:12"))));
        }
        else{
            fail();
        }
    }
    public static void doubleFriendAdd(Repository<Long, User> userRepo, Repository<Pair<Long>, Friendship> friendshipRepo){
        assertEquals(2,friendshipRepo.size());
        if(userRepo.findElement(1L).isPresent() && userRepo.findElement(2L).isPresent() && userRepo.findElement(3L).isPresent()) {
            assertEquals(1, userRepo.findElement(1L).get().getFriends().size());
            assertTrue(userRepo.findElement(1L).get().getFriends().contains(userRepo.findElement(3L).get()));

            assertEquals(2, userRepo.findElement(3L).get().getFriends().size());
            assertTrue(userRepo.findElement(3L).get().getFriends().contains(userRepo.findElement(1L).get()));
            assertTrue(userRepo.findElement(3L).get().getFriends().contains(userRepo.findElement(2L).get()));
        }
        else{
            fail();
        }
    }
    public static void friendshipDelete(Repository<Long, User> userRepo, Repository<Pair<Long>, Friendship> friendshipRepo){
        friendshipRepo.deleteElement(new Pair<>(1L,3L));
        if(userRepo.findElement(1L).isPresent() && userRepo.findElement(3L).isPresent()) {
            assertEquals(0, userRepo.findElement(1L).get().getFriends().size());
            assertEquals(1, userRepo.findElement(3L).get().getFriends().size());
            assertFalse(userRepo.findElement(1L).get().getFriends().contains(new User(3L, "Phil Smith", "philsmith@gmail.com")));
        }
        else{
            fail();
        }
    }
}

package Tests.RepositoryTests.ConcreteTests.Memory;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.FriendshipFileRepository;
import Repository.ConcreteRepositories.MemoryFriendshipRepository;
import Repository.ConcreteRepositories.MemoryUserRepository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;
import Tests.RepositoryTests.ConcreteTests.ConcreteRepoTestHelper;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MemoryFriendshipRepoTest {
    private MemoryUserRepository createUserRepo(Validator<User> validator){
        MemoryUserRepository userRepo = new MemoryUserRepository(validator);
        userRepo.addElement(new User(1L,"John Doe","johndoe@gmail.com"));
        userRepo.addElement(new User(2L,"Phil Smith","philsmith@gmail.com"));
        userRepo.addElement(new User(3L,"Luke Scott","lukescott@gmail.com"));
        return userRepo;
    }
    private MemoryFriendshipRepository createFriendshipRepo(Validator<Friendship> validator, MemoryUserRepository userRepo){
        MemoryFriendshipRepository friendshipRepo = new MemoryFriendshipRepository(userRepo,validator);
        if(userRepo.findElement(1L).isPresent() && userRepo.findElement(2L).isPresent() && userRepo.findElement(3L).isPresent()) {
            friendshipRepo.addElement(new Friendship(userRepo.findElement(1L).get(), userRepo.findElement(3L).get(), LocalDateTime.parse("2025-12-13T17:02:22")));
            friendshipRepo.addElement(new Friendship(userRepo.findElement(3L).get(), userRepo.findElement(2L).get(), LocalDateTime.parse("2024-07-01T21:03:24")));
            return friendshipRepo;
        }
        else{
            throw new RepositoryException("Wrong test initialization!",new InvalidParameterException(""));
        }
    }
    @Test
    public void testCascadeDelete(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        MemoryUserRepository user_repo = createUserRepo(userValidator);
        MemoryFriendshipRepository friendship_repo = createFriendshipRepo(friendshipValidator,user_repo);

        ConcreteRepoTestHelper.cascadeDelete(user_repo,friendship_repo);
    }

    @Test
    public void testCascadeModify(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        MemoryUserRepository user_repo = createUserRepo(userValidator);
        MemoryFriendshipRepository friendship_repo = createFriendshipRepo(friendshipValidator,user_repo);

        ConcreteRepoTestHelper.cascadeModify(user_repo,friendship_repo);
    }

    @Test
    public void testErrorAdd(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        MemoryUserRepository user_repo = createUserRepo(userValidator);
        MemoryFriendshipRepository friendship_repo = createFriendshipRepo(friendshipValidator,user_repo);

        ConcreteRepoTestHelper.errorAdd(user_repo,friendship_repo);
    }
    @Test
    public void testDoubleFriendAdd(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        MemoryUserRepository userRepo = createUserRepo(userValidator);
        MemoryFriendshipRepository friendship_repo = createFriendshipRepo(friendshipValidator,userRepo);

        ConcreteRepoTestHelper.doubleFriendAdd(userRepo,friendship_repo);
    }
    @Test
    public void testFriendshipDelete(){
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        MemoryUserRepository userRepo = createUserRepo(userValidator);
        MemoryFriendshipRepository friendshipRepo = createFriendshipRepo(friendshipValidator,userRepo);

        ConcreteRepoTestHelper.friendshipDelete(userRepo,friendshipRepo);
    }
}

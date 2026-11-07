package Tests.RepositoryTests.AbstractTests;

import Domain.Objects.User;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Repository.AbstractRepositories.MemoryRepository;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemoryRepositoryTest {

    @Test
    void testFindAll(){
        Repository<Long, User> new_repo = new MemoryRepository<Long, User>(new UserValidator());
        User[] new_users = {new User(2L,"John Doe","johndoe@gmail.com"),
                new User(3L,"Luke Doe","lukedoe@gmail.com"),
                new User(1L,"Final Doe","finaldoe@gmail.com")};
        for(User u : new_users){
            new_repo.addElement(u);
        }
        Iterable<User> iter = new_repo.findAll((_) -> true);
        for(User u : new_users){
            boolean found = false;
            for(User u2 : iter){
                if(u.equals(u2)){
                    found = true;
                    break;
                }
            }
            if(!found){
                fail();
            }
        }
        List<User> list = new_repo.findAll((u) -> u.getName().startsWith("L"));
        assertEquals(1,list.size());
        assertEquals("Luke Doe",list.getFirst().getName());
    }

    @Test
    void testFindElement() {
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        Repository<Long, User> new_repo = new MemoryRepository<Long, User>(new UserValidator());
        new_repo.addElement(new_user);
        assertEquals(Optional.of(new_user),new_repo.findElement(2L));
        assertEquals(Optional.empty(),new_repo.findElement(3L));
        assertThrows(RepositoryException.class,() -> new_repo.findElement(null));
    }

    @Test
    void testAddElement() {
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User wrong_user = new User(3L,"John Doe","");
        Repository<Long, User> new_repo = new MemoryRepository<Long, User>(new UserValidator());

        assertEquals(Optional.of(new_user),new_repo.addElement(new_user));

        var iterator = new_repo.findAll((_) -> true).iterator();

        assertEquals(iterator.next(),new_user);
        assertEquals(Optional.of(new_user),new_repo.findElement(2L));

        assertEquals(Optional.empty(),new_repo.addElement(new_user));
        assertThrows(RepositoryException.class,() -> new_repo.addElement(null));
        new_user.setId(null);
        assertThrows(RepositoryException.class,() -> new_repo.addElement(new_user));
        assertThrows(ValidationException.class,() -> new_repo.addElement(wrong_user));
    }

    @Test
    void testDeleteElement() {
        Repository<Long, User> new_repo = new MemoryRepository<Long, User>(new UserValidator());

        User[] new_users = {new User(2L,"John Doe","johndoe@gmail.com"),
                new User(3L,"Luke Doe","lukedoe@gmail.com")};
        for(User u : new_users){
            new_repo.addElement(u);
        }
        assertEquals(Optional.empty(),new_repo.deleteElement(null));
        assertEquals(Optional.of(new_users[0]),new_repo.deleteElement(2L));

        Iterable<User> iter = new_repo.findAll((_) -> true);

        assertEquals(Optional.empty(),new_repo.findElement(2L));
        assertEquals(Optional.of(iter.iterator().next()),new_repo.findElement(3L));
    }

    @Test
    void testModifyElement() {
        Repository<Long, User> new_repo = new MemoryRepository<Long, User>(new UserValidator());
        User[] new_users = {new User(2L,"John Doe","johndoe@gmail.com"),
                new User(3L,"Luke Doe","lukedoe@gmail.com")};
        for(User u : new_users){
            new_repo.addElement(u);
        }
        assertThrows(RepositoryException.class, () -> new_repo.modifyElement(new User(null,"Mike Doe","mikedoe@gmail.com")));
        assertThrows(RepositoryException.class, () -> new_repo.modifyElement(null));
        User wrongUser = new User(1L,"Mike Doe","mikedoe@gmail.com");

        assertEquals(Optional.of(wrongUser),new_repo.modifyElement(wrongUser));
        new_repo.modifyElement(new User(2L,"Mike Doe","mikedoe@gmail.com"));
        if(new_repo.findElement(2L).isPresent()) {
            assertEquals("Mike Doe", new_repo.findElement(2L).get().getName());
        }
        else{
            fail();
        }
        if(new_repo.findElement(3L).isPresent()) {
            assertNotEquals("Mike Doe", new_repo.findElement(3L).get().getName());
        }
        else{
            fail();
        }
    }
}
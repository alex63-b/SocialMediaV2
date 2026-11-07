package Tests.ServiceTests;

import Domain.Objects.User;
import Domain.Validation.UserValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.Exceptions.RepositoryException;
import Service.UserServices.BasicUserService;
import Service.UserServices.ConsoleUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BasicUserServiceTest {
    String test_path = "test_data/Users/user_data.csv";
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(test_path));
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
    void testAddUser() {
        Validator<User> validator = new UserValidator();
        Repository<Long, User> repo = new UserFileRepository(validator,test_path);
        ConsoleUserService service = new ConsoleUserService(repo);

        assertThrows(ValidationException.class,() -> service.addUser("Killie Davis",""));

        service.addUser("Killie Davis","killiedavis");
        assertEquals(4,repo.size());
        if(repo.findElement(4L).isPresent()) {
            assertEquals("Killie Davis", repo.findElement(4L).get().getName());
        }
        else{
            fail();
        }
    }

    @Test
    void testDeleteUser() {
        Validator<User> validator = new UserValidator();
        Repository<Long, User> repo = new UserFileRepository(validator,test_path);
        ConsoleUserService service = new ConsoleUserService(repo);

        assertThrows(ValidationException.class,() -> service.deleteUser(9L));
        service.deleteUser(2L);
        assertEquals(2,repo.size());
        assertEquals(Optional.empty(),repo.findElement(2L));
    }
    @Test
    void searchByName(){
        Validator<User> validator = new UserValidator();
        Repository<Long, User> repo = new UserFileRepository(validator,test_path);
        BasicUserService service = new BasicUserService(repo);

        List<User> testList = service.searchByName("e");
        assertEquals(2,testList.size());
        assertEquals("John Doe",testList.getFirst().getName());
        assertEquals("Luke Scott",testList.getLast().getName());

        assertEquals(0,service.searchByName("fdjfajfkfajk").size());
    }
}

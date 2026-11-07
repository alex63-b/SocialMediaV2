package Tests.RepositoryTests.ConcreteTests.File;

import Domain.Objects.User;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.UserFileRepository;
import Repository.Helpers.DefaultFilters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserFileRepositoryTest {
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
    void testLoad() {
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = new UserFileRepository(validator,"test_data/Users/user_data.csv");

        new UserFileRepository(validator, "test_data/Users/corrupted_data.csv");
        new UserFileRepository(validator, "test_data/Users/corrupted_data_2.csv");
        new UserFileRepository(validator, "test_data/Users/corrupted_data_3.csv");

        Repository<Long, User> empty_repo = new UserFileRepository(validator,"a");
        assertEquals(0,empty_repo.size());

        assertEquals(3,new_repo.size());
        if(new_repo.findElement(1L).isPresent() && new_repo.findElement(2L).isPresent() && new_repo.findElement(3L).isPresent()) {
            assertEquals("John Doe", new_repo.findElement(1L).get().getName());
            assertEquals("philsmith@gmail.com", new_repo.findElement(2L).get().getFirstName());
            assertEquals("Luke Scott", new_repo.findElement(3L).get().getName());
        }
        else{
            fail();
        }
    }

    @Test
    void testStore() {
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = new UserFileRepository(validator,"test_data/Users/user_data.csv");

        new_repo.addElement(new User(4L,"Mike Forge","mikeforge@gmail.com"));

        Repository<Long,User> test_repo = new UserFileRepository(validator,"test_data/Users/user_data.csv");
        assertEquals(4,test_repo.size());
        for(User u : test_repo.findAll(DefaultFilters::filterTrue)){
            assertEquals(Optional.of(u),new_repo.findElement(u.getId()));
        }
    }
}
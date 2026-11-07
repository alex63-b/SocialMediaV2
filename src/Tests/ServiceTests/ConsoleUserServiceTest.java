package Tests.ServiceTests;

import Domain.Objects.User;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.UserFileRepository;
import Service.UserServices.ConsoleUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleUserServiceTest {
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
            writer.write("1,John Doe,Macaroni\n" +
                    "2,Phil Smith,Coulson\n" +
                    "3,Luke Scott,Smithson");
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testShowAllUsers(){
        Validator<User> validator = new UserValidator();
        Repository<Long, User> repo = new UserFileRepository(validator,test_path);
        ConsoleUserService service = new ConsoleUserService(repo);
        assertEquals("1. The user John Doe Macaroni;\n2. The user Phil Smith Coulson;\n3. The user Luke Scott Smithson;\n",service.showAllUsers());
    }
}
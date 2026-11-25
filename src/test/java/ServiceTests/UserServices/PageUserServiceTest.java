package ServiceTests.UserServices;

import Domain.Objects.User;
import Domain.Validation.IntegrityValidation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Factory.RepositoryFactory;
import Service.Factories.ServiceFactory;
import Service.UserServices.BasicUserService;
import Service.UserServices.PageUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.factory.PropertyFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PageUserServiceTest {
    private final static String url = "jdbc:postgresql://localhost:5432/SMTestDB";
    private final static String user = "postgres";
    private final static String password = "postgres";
    @BeforeEach
    public void setUp(){
        reloadDBData();
    }
    @AfterEach
    public void tearDown(){
        reloadDBData();
    }

    private void reloadDBData(){
        List<String> nameList = List.of("John Doe","Phil Smith","Luke Scott");
        List<String> firstNameList = List.of("johndoe@gmail.com","philsmith@gmail.com","lukescott@gmail.com");
        String insertSql = "INSERT INTO users(first_name,last_name) VALUES (?,?)";

        try(Connection connection = DriverManager.getConnection(url,user,password)){
            PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users");
            statementDelete.executeUpdate();
            PreparedStatement statementResetId = connection.prepareStatement("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statementResetId.executeUpdate();
            IntStream.range(0,3).forEach((i) -> {
                try {
                    PreparedStatement statement = connection.prepareStatement(insertSql);
                    statement.setString(2, nameList.get(i));
                    statement.setString(1, firstNameList.get(i));
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    throw new RepositoryException("Something went wrong!",e.getCause());
                }
            });
        }
        catch (SQLException e){
            throw new RepositoryException("Something went wrong!",e);
        }
    }

    @Test
    void findOnPage() {
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        Properties properties = propertyFactory.propertiesFromConfigFile("test_data/Resources/db_test.properties");
        RepositoryFactory factory = new RepositoryFactory(properties);
        Validator<User> validator = new UserValidator();
        Repository<Long, User> new_repo = factory.createUserRepository(validator);

        ServiceFactory serviceFactory = new ServiceFactory(properties);
        BasicUserService userService = serviceFactory.createUserService(new_repo);

        assertInstanceOf(PageUserService.class,userService);

        PageUserService service = (PageUserService) userService;

        assertThrows(IllegalArgumentException.class,() -> service.findOnPage(-14));

        List<User> page1 = service.findOnPage(0);
        assertEquals(2,page1.size());
        assertEquals("John Doe",page1.getFirst().getName());
        assertEquals("Phil Smith",page1.getLast().getName());

        List<User> page2 = service.findOnPage(1);
        assertEquals(1,page2.size());
        assertEquals("Luke Scott",page2.getFirst().getName());

        List<User> page3 = service.findOnPage(2);
        assertEquals(0,page3.size());
    }
}
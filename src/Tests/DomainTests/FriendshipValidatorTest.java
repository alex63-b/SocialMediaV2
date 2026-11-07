package Tests.DomainTests;

import Domain.Objects.Friendship;
import Domain.Objects.User;
import Domain.Validation.FriendshipValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipValidatorTest {
    @Test
    public void testValidate(){
        Validator<Friendship> validator = new FriendshipValidator();
        User[] new_users = {new User(2L,"John Doe","johndoe@gmail.com"),
                new User(3L,"Luke Doe","lukedoe@gmail.com"),
                new User(1L,"Final Doe","finaldoe@gmail.com")};
        User errorUser = new User(-1L,"Glitchy","Gremlin");
        assertThrows(ValidationException.class,() -> validator.validate(new Friendship(new_users[0],new_users[0], LocalDateTime.now())));
        assertThrows(ValidationException.class,() -> validator.validate(new Friendship(new_users[1],new_users[0],
                LocalDateTime.of(2027,12,31,10,3,45))));
        assertThrows(ValidationException.class,() -> validator.validate(new Friendship(errorUser,new_users[2],LocalDateTime.now())));
        assertThrows(ValidationException.class,() -> validator.validate(new Friendship(new_users[2],errorUser,LocalDateTime.now())));
        validator.validate(new Friendship(new_users[0],new_users[1],LocalDateTime.now()));
        validator.validate(new Friendship(new_users[1],new_users[2],LocalDateTime.now()));
    }
}
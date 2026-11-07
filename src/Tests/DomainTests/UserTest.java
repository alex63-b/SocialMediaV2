package Tests.DomainTests;
import Domain.Objects.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void test_init(){
        User new_user = new User(1L,"lol","a@b.c");
        assertEquals("a@b.c", new_user.getFirstName());
        assertEquals("lol",new_user.getName());
        assertEquals(1L,new_user.getId());
        assertTrue(new_user.getFriends().isEmpty());
    }
    @Test
    void test_toString(){
        User new_user = new User(2L,"John Doe","Macaroni");
        assertEquals("2. The user John Doe Macaroni;",new_user.toString());
    }
    @Test
    void test_addFriend(){
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User friend = new User(3L,"Luke Castellan","hermes@gmail.com");
        new_user.addFriend(friend);
        assertEquals(1,new_user.getFriends().size());
    }
    @Test
    void test_removeFriend(){
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User friend = new User(3L,"Luke Castellan","hermes@gmail.com");
        new_user.addFriend(friend);
        new_user.removeFriend(friend);
        assertEquals(0,new_user.getFriends().size());
    }
    @Test
    void testEqualsEveryField(){
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User friend = new User(2L,"Luke Castellan","hermes@gmail.com");
        User friend2 = new User(2L,"Luke Castellan","herme@gmail.com");
        User friend3 = new User(2L,"Luke Castella","hermes@gmail.com");
        assertTrue(new_user.equalsEveryField(new_user));
        assertFalse(new_user.equalsEveryField(friend));
        assertFalse(friend.equalsEveryField(friend2));
        assertFalse(friend3.equalsEveryField(friend));
    }
}
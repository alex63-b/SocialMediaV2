package Tests.DomainTests;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FriendshipTest {
    @Test
    public void testInit(){
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User friend = new User(3L,"Luke Castellan","hermes@gmail.com");
        LocalDateTime date = LocalDateTime.of(2024, Month.AUGUST,23,15,32,33);
        Friendship friendship = new Friendship(new_user,friend,date);
        assertEquals(new_user,friendship.getFriend1());
        assertEquals(friend,friendship.getFriend2());
        assertEquals(date,friendship.getDate());
        assertEquals(new Pair<>(2L,3L),friendship.getId());
    }
    @Test
    public void testToString(){
        User new_user = new User(2L,"John Doe","johndoe@gmail.com");
        User friend = new User(3L,"Luke Castellan","hermes@gmail.com");
        Friendship friendship = new Friendship(new_user,friend, LocalDateTime.of(2024, Month.AUGUST,23,15,32,33));
        assertEquals("Friendship between John Doe(ID 2) and Luke Castellan(ID 3) started on the date 23 AUGUST 2024, at 15:32:33;",friendship.toString());
    }
}

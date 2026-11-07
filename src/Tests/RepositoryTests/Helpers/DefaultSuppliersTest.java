package Tests.RepositoryTests.Helpers;

import Domain.Objects.Friendship;
import Domain.Objects.User;
import Repository.Helpers.DefaultSuppliers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSuppliersTest {

    @Test
    void get0() {
        User friend1 = new User(1L,"a","b");
        User friend2 = new User(2L, "c","d");
        Friendship friendship = new Friendship(friend1,friend2, LocalDateTime.now());
        assertEquals(0,DefaultSuppliers.get0(friend1));
        assertEquals(0,DefaultSuppliers.get0(friend2));
        assertEquals(0,DefaultSuppliers.get0(friendship));
        assertEquals(0,DefaultSuppliers.get0(null));
    }
    @Test
    void getIdentity() {
        User friend1 = new User(1L,"a","b");
        User friend2 = new User(2L, "c","d");
        Friendship friendship = new Friendship(friend1,friend2, LocalDateTime.now());
        assertEquals(friend1,DefaultSuppliers.getIdentity(friend1));
        assertEquals(friend2,DefaultSuppliers.getIdentity(friend2));
        assertEquals(friendship,DefaultSuppliers.getIdentity(friendship));
        assertNull(DefaultSuppliers.getIdentity(null));
    }
}
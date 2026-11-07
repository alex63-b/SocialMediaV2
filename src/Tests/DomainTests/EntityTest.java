package Tests.DomainTests;

import Domain.Objects.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {
    @Test
    void test_init(){
        Entity<Long> entity = new Entity<>(16L);
        assertEquals(16L,entity.getId());
        entity.setId(24L);
        assertEquals(24L,entity.getId());
    }
    @Test
    void test_equals(){
        Entity<Integer> entity = new Entity<>(25);
        Entity<Integer> entity2 = new Entity<>(50);
        assertNotEquals(entity, entity2);
        assertEquals(entity,entity);
        assertEquals(entity2,entity2);
    }
}
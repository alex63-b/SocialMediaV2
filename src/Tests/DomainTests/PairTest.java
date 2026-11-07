package Tests.DomainTests;

import Domain.Objects.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    void testInit(){
        Pair<Long> idPair = new Pair<>(1L,2L);
        assertEquals(1L,idPair.getFirstElement());
        assertEquals(2L,idPair.getSecondElement());
    }
    @Test
    void testEquals() {
        Pair<Long> idPair1 = new Pair<>(1L,2L);
        Pair<Long> idPairRev1 = new Pair<>(2L,1L);
        Pair<Long> idPair2 = new Pair<>(3L,4L);

        assertEquals(idPair1,idPair1);
        assertNotEquals(idPair1,idPair2);
        assertEquals(idPair1,idPairRev1);
        assertNotEquals(idPairRev1,idPair2);
    }
    @Test
    void testHashCode() {
        Pair<Long> idPair1 = new Pair<>(1L,2L);
        Pair<Long> idPairRev1 = new Pair<>(2L,1L);
        Pair<Long> idPair2 = new Pair<>(3L,4L);

        assertEquals(idPair1.hashCode(),idPair1.hashCode());
        assertNotEquals(idPair1.hashCode(),idPair2.hashCode());
        assertEquals(idPair1.hashCode(),idPairRev1.hashCode());
        assertNotEquals(idPairRev1.hashCode(),idPair2.hashCode());
    }
}
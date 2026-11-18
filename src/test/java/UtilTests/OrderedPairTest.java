package UtilTests;

import util.datatypes.OrderedPair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderedPairTest {
    @Test
    void testInit(){
        OrderedPair<Long> idPair = new OrderedPair<>(1L,2L);
        assertEquals(1L,idPair.getFirstElement());
        assertEquals(2L,idPair.getSecondElement());
    }
    @Test
    void testEquals() {
        OrderedPair<Long> idPair1 = new OrderedPair<>(1L,2L);
        OrderedPair<Long> idPairRev1 = new OrderedPair<>(2L,1L);
        OrderedPair<Long> idPair2 = new OrderedPair<>(3L,4L);

        assertEquals(idPair1,idPair1);
        assertNotEquals(idPair1,idPair2);
        assertEquals(idPair1,idPairRev1);
        assertNotEquals(idPairRev1,idPair2);
    }
    @Test
    void testHashCode() {
        OrderedPair<Long> idPair1 = new OrderedPair<>(1L,2L);
        OrderedPair<Long> idPairRev1 = new OrderedPair<>(2L,1L);
        OrderedPair<Long> idPair2 = new OrderedPair<>(3L,4L);

        assertEquals(idPair1.hashCode(),idPair1.hashCode());
        assertNotEquals(idPair1.hashCode(),idPair2.hashCode());
        assertEquals(idPair1.hashCode(),idPairRev1.hashCode());
        assertNotEquals(idPairRev1.hashCode(),idPair2.hashCode());
    }
    @Test
    void testCompareTo(){
        OrderedPair<Long> idPair1 = new OrderedPair<>(1L,2L);
        OrderedPair<Long> idPairRev1 = new OrderedPair<>(2L,1L);
        OrderedPair<Long> idPair2 = new OrderedPair<>(3L,4L);
        OrderedPair<Long> idPair3 = new OrderedPair<>(1L,3L);

        assertTrue(idPair1.compareTo(idPair2) < 0);
        assertEquals(0, idPair1.compareTo(idPairRev1));
        assertTrue(idPair3.compareTo(idPair1) > 0);
    }
}
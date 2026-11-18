package UtilTests;

import util.datatypes.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    void testInit(){
        Pair<Integer,String> newPair = new Pair<>(12,"abc");
        assertEquals(12,newPair.getFirst());
        assertEquals("abc",newPair.getSecond());
    }

    @Test
    void testEquals() {
        Pair<Integer,String> pair1 = new Pair<>(12,"abc");
        Pair<Integer,String> pair2 = new Pair<>(12,"abd");
        Pair<String,String> pair3 = new Pair<>("12","abc");
        Pair<Integer,String> pair4 = new Pair<>(13,"abc");
        assertNotEquals("a", pair1);
        assertEquals(pair1, pair1);
        assertNotEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
        assertNotEquals(pair1, pair4);
        assertNotEquals(pair2, pair4);
    }

    @Test
    void testHashCode() {
        Pair<Integer,String> pair1 = new Pair<>(12,"abc");
        Pair<Integer,String> pair2 = new Pair<>(12,"abd");
        Pair<String,String> pair3 = new Pair<>("12","abc");
        Pair<Integer,String> pair4 = new Pair<>(13,"abc");

        assertNotEquals(pair1.hashCode(),pair2.hashCode());
        assertNotEquals(pair1.hashCode(),pair3.hashCode());
        assertNotEquals(pair1.hashCode(),pair4.hashCode());
    }
}
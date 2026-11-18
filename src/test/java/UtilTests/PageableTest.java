package UtilTests;

import org.junit.jupiter.api.Test;
import util.paging.Pageable;

import static org.junit.jupiter.api.Assertions.*;

class PageableTest {
    @Test
    public void testInit(){
        Pageable pageable = new Pageable(12,4);
        assertEquals(12,pageable.getPageNumber());
        assertEquals(4,pageable.getPageSize());
    }
}
package UtilTests;

import Domain.Objects.User;
import org.junit.jupiter.api.Test;
import util.paging.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {
    @Test
    public void testInit(){
        List<User> elements = List.of(new User(1L,"John","Doe"),new User(2L,"Phil","Smith"));
        Page<User> new_page = new Page<>(elements,elements.size());
        assertEquals(elements,new_page.getElementsOnPage());
        assertEquals(elements.size(),new_page.getTotalNumberOfElements());
    }
}
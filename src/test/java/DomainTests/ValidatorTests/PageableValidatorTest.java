package DomainTests.ValidatorTests;

import Domain.Validation.PageableValidator;
import Domain.Validation.Validator;
import org.junit.jupiter.api.Test;
import util.paging.Pageable;

import static org.junit.jupiter.api.Assertions.*;

class PageableValidatorTest {

    @Test
    void testValidate() {
        Validator<Pageable> validator = new PageableValidator();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Pageable(-1,2)));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Pageable(1,-2)));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new Pageable(1,0)));

        validator.validate(new Pageable(0,1));
        validator.validate(new Pageable(143,125));
    }
}
package DomainTests.ValidatorTests;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import Domain.Validation.IntegrityValidation.FriendRequestValidator;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestValidatorTest {
    @Test
    void testValidate(){
        Validator<FriendRequest> lightValidator = new FriendRequestValidator();
        FriendRequest correctRequest = new FriendRequest(1L,2L,3L, FriendRequestStates.PENDING,LocalDateTime.parse("2025-12-13T17:02:22"));
        List<FriendRequest> errorRequests = List.of(new FriendRequest(-1L,2L,3L, FriendRequestStates.PENDING,LocalDateTime.parse("2025-12-13T17:02:22")),
        new FriendRequest(1L,-2L,3L, FriendRequestStates.PENDING,LocalDateTime.parse("2025-12-13T17:02:22")),
        new FriendRequest(1L,2L,-3L, FriendRequestStates.PENDING,LocalDateTime.parse("2025-12-13T17:02:22")),
                new FriendRequest(1L,2L,3L, null, LocalDateTime.parse("2025-12-13T17:02:22")),
        new FriendRequest(1L,2L,3L, FriendRequestStates.PENDING,LocalDateTime.parse("2029-12-13T17:02:22")));

        lightValidator.validate(correctRequest);

        errorRequests.forEach((friendRequest) ->{
            assertThrows(ValidationException.class,() -> lightValidator.validate(friendRequest));
        });
    }
}
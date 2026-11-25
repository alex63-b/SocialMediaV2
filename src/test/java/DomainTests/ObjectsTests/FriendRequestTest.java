package DomainTests.ObjectsTests;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestTest {
    @Test
    public void testInit(){
        FriendRequest newRequest = new FriendRequest(1L,2L,3L, FriendRequestStates.PENDING, LocalDateTime.parse("2025-12-13T17:02:22"));
        assertEquals(LocalDateTime.parse("2025-12-13T17:02:22"),newRequest.getDate());
        assertEquals(2L,newRequest.getFrom());
        assertEquals(3L,newRequest.getTo());
        assertEquals(FriendRequestStates.PENDING,newRequest.getStatus());
    }
    @Test
    public void testApproveReject(){
        FriendRequest newRequest = new FriendRequest(1L,2L,3L, FriendRequestStates.PENDING, LocalDateTime.parse("2025-12-13T17:02:22"));
        newRequest.approve();
        assertEquals(FriendRequestStates.APPROVED,newRequest.getStatus());
        newRequest.reject();
        assertEquals(FriendRequestStates.REJECTED,newRequest.getStatus());
        newRequest.approve();
        assertEquals(FriendRequestStates.APPROVED,newRequest.getStatus());
    }
}
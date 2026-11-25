package Domain.Objects;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Long>{
    private final Long from;
    private final Long to;
    private FriendRequestStates status;
    private final LocalDateTime date;
    /**
     * This function creates an entity with the specified identifier.
     *
     * @param id the identifier to assign to the entity;
     */
    public FriendRequest(Long id, Long from, Long to, FriendRequestStates status, LocalDateTime date) {
        super(id);
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
    }

    public Long getTo() {
        return to;
    }

    public Long getFrom() {
        return from;
    }

    public FriendRequestStates getStatus() {
        return status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void reject(){
        status = FriendRequestStates.REJECTED;
    }

    public void approve(){
        status = FriendRequestStates.APPROVED;
    }
}

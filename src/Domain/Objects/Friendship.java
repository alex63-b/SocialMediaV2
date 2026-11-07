package Domain.Objects;

import java.time.LocalDateTime;

/**
 * This class represents a friendship between two users, identified by their IDs and associated with a start date.
 */
public class Friendship extends Entity<Pair<Long>> {
    private final User friend1;
    private final User friend2;
    private final LocalDateTime date;

    /**
     * This function creates a friendship between two users with the specified start date.
     * @param friend1 the first user in the friendship;
     * @param friend2 the second user in the friendship;
     * @param date the date and time when the friendship started;
     */
    public Friendship(User friend1, User friend2, LocalDateTime date) {
        super(new Pair<>(friend1.getId(),friend2.getId()));
        this.date = date;
        this.friend1 = friend1;
        this.friend2 = friend2;
    }

    /**
     * This function returns a string representation of the friendship.
     * @return a description of the two users and when their friendship started;
     */
    @Override
    public String toString(){
        String hour = Integer.toString(date.getHour());
        if(date.getHour() < 10){
            hour = "0" + hour;
        }
        String minute = Integer.toString(date.getMinute());
        if(date.getMinute() < 10){
            minute = "0" + minute;
        }
        String second = Integer.toString(date.getSecond());
        if(date.getSecond() < 10){
            second = "0" + second;
        }
        return "Friendship between " + friend1.getName() + "(ID " + friend1.getId() + ") and " + friend2.getName() + "(ID " + friend2.getId() + ") started on the date "
                + date.getDayOfMonth() + " " + date.getMonth() + " " + date.getYear() + ", at " + hour + ":" + minute + ":" + second + ";";
    }

    /**
     * This function returns the first user in the friendship.
     * @return the first user in the friendship;
     */
    public User getFriend1() {
        return friend1;
    }

    /**
     * This function returns the second user in the friendship.
     * @return the second user in the friendship;
     */
    public User getFriend2() {
        return friend2;
    }

    /**
     * This function returns the date and time when the friendship started.
     * @return the friendship's start date and time;
     */
    public LocalDateTime getDate() {
        return date;
    }
}

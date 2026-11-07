package Domain.Objects;

import java.util.HashSet;

/**
 * This class represents a user with a name, first name, ID, and a set of friends.
 */
public class User extends Entity<Long>{
    private final String name;
    private final String firstName;
    private final HashSet<User> friends;

    /**
     * This function creates a user with the specified ID, name, and first name.
     * @param id the user's ID;
     * @param name the user's name;
     * @param firstName the user's first name;
     */
    public User(Long id, String name, String firstName){
        super(id);
        this.name = name;
        this.firstName = firstName;
        friends = new HashSet<>();
    }

    /**
     * This function checks whether another user has the same name and first name.
     * @param user2 the user to compare with;
     * @return {@code true} if both users have the same name and first name,
     *         {@code false} otherwise;
     */
    public boolean equalsEveryField(User user2){
        return name.equals(user2.name) && firstName.equals(user2.getFirstName());
    }

    /**
     * This function returns the user's name.
     * @return the user's name;
     */
    public String getName() {
        return name;
    }

    /**
     * This function returns the user's first name.
     * @return the user's first name;
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This function returns the set of users who are friends with this user.
     * @return the set of the user's friends;
     */
    public HashSet<User> getFriends(){
        return friends;
    }

    /**
     * This function adds a user to this user's set of friends.
     * @param newFriend the user to add as a friend;
     */
    public void addFriend(User newFriend){
        friends.add(newFriend);
    }

    /**
     * This function removes a user from this user's set of friends.
     * @param newFriend the user to remove from the set of friends;
     */
    public void removeFriend(User newFriend){
        friends.remove(newFriend);
    }

    /**
     * This function returns a string representation of the user.
     * @return a description containing the user's ID, name, and first name;
     */
    @Override
    public String toString(){
        return id + ". The user " + name + " " + firstName + ";";
    }
}

package Service.FriendshipServices;

import Domain.Objects.Friendship;
import Domain.Objects.Pair;
import Domain.Objects.User;
import Repository.AbstractRepositories.Repository;
import Repository.Helpers.DefaultFilters;

/**
 * This class adds console-oriented display functionality to the friendship service.
 */
public class ConsoleFriendshipService extends BasicFriendshipService {
    /**
     * This function creates a console friendship service using the specified repositories.
     * @param friendshipRepo the repository used to store and retrieve friendships;
     * @param userRepo the repository used to store and retrieve users;
     */
    public ConsoleFriendshipService(Repository<Pair<Long>, Friendship> friendshipRepo, Repository<Long, User> userRepo) {
        super(friendshipRepo,userRepo);
    }

    /**
     * This function returns a string containing all friendships in the repository.
     * @return the friendships, one per line;
     */
    public String showAllFriendships(){
        StringBuilder text = new StringBuilder();
        friendshipRepo.findAll(DefaultFilters::filterTrue).stream().forEach((f) -> {
            text.append(f.toString()).append("\n");
        });
        return text.toString();
    }
}

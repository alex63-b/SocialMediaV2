package Service.FriendshipServices;

import Domain.Objects.Friendship;
import Repository.ConcreteRepositories.DB.FriendshipDBRepository;
import util.datatypes.OrderedPair;
import Domain.Objects.User;
import Repository.AbstractRepositories.PagingRepository;
import Repository.AbstractRepositories.Repository;
import util.paging.Pageable;

import java.util.List;

/**
 * Provides paged friendship operations for a database-backed friendship repository.
 */
public class PageFriendshipService extends BasicFriendshipService{
    final int pageSize;
    /**
     * This function creates a friendship service using the specified repositories.
     *
     * @param repo     the repository used to store and retrieve friendships;
     * @param userRepo the repository used to store and retrieve users;
     */
    public PageFriendshipService(PagingRepository<OrderedPair<Long>, Friendship> repo, Repository<Long, User> userRepo, int pageSize) {
        super(repo, userRepo);
        this.pageSize = pageSize;
    }

    /**
     * This function returns a list of the friendships found on a given page.
     * @param pageNumber an integer that represents the page number, starts from 0;
     * @return A list of the friendships on that page.
     * @throws IllegalArgumentException if the page number is negative(shouldn't happen naturally)
     */
    public List<Friendship> findOnPage(int pageNumber) throws IllegalArgumentException{
        FriendshipDBRepository friendRepo = (FriendshipDBRepository) friendshipRepo;
        return (List<Friendship>) friendRepo.findAllOnPage(new Pageable(pageNumber,pageSize)).getElementsOnPage();
    }

    /**
     * This function returns a list of users that is the friend list of an user;
     * @param userId an integer that represents the ID of the user;
     * @param pageNumber an integer that represents the page number, starts from 0;
     * @return A list of the user's friends;
     * @throws IllegalArgumentException if the page number is negative(shouldn't happen naturally)
     */

    public List<User> getUserFriendships(Long userId, int pageNumber){
        FriendshipDBRepository friendRepo = (FriendshipDBRepository) friendshipRepo;
        return (List<User>) friendRepo.findFriendsOnPage(userId, new Pageable(pageNumber,pageSize)).getElementsOnPage();
    }
}

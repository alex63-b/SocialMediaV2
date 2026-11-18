package Service.UserServices;

import Domain.Objects.User;
import Repository.AbstractRepositories.PagingRepository;
import Repository.AbstractRepositories.Repository;
import Repository.ConcreteRepositories.DB.UserDBRepository;
import util.factory.PropertyFactory;
import util.paging.Pageable;

import java.util.List;

/**
 * Provides paged user operations for a paging repository.
 */
public class PageUserService extends BasicUserService{
    private final int pageSize;
    private final PagingRepository<Long,User> userRepo;
    /**
     * This function creates a user service and sets the next available user ID.
     *
     * @param repository the repository used to store and retrieve users;
     */
    public PageUserService(PagingRepository<Long, User> repository, int pageSize) {
        this.pageSize = pageSize;
        this.userRepo = repository;
        super(repository);
    }

    /**
     * This function returns a list of the users found on a given page.
     * @param pageNumber an integer that represents the page number, starts from 0;
     * @return A list of the users on that page.
     * @throws IllegalArgumentException if the page number is negative(shouldn't happen naturally)
     */
    public List<User> findOnPage(int pageNumber) throws IllegalArgumentException{
        return (List<User>) userRepo.findAllOnPage(new Pageable(pageNumber,pageSize)).getElementsOnPage();
    }
}

package Service.UserServices;

import Domain.Objects.User;
import Repository.AbstractRepositories.Repository;
import Repository.Helpers.DefaultFilters;

/**
 * This class adds console-oriented display functionality to the user service.
 */
public class ConsoleUserService extends BasicUserService {
    /**
     * This function creates a console user service for the specified repository.
     * @param repository the repository used to store and retrieve users;
     */
    public ConsoleUserService(Repository<Long, User> repository){
        super(repository);
    }

    /**
     * This function returns a string containing all users in the repository.
     * @return the string form of the users, one per line;
     */
    public String showAllUsers(){
        StringBuilder result = new StringBuilder();
        repository.findAll(DefaultFilters::filterTrue)
                .forEach((u) -> result.append(u).append("\n"));
        return result.toString();
    }
}

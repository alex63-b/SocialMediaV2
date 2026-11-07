package Service.UserServices;

import Domain.Objects.User;
import Domain.Validation.ValidationException;
import Domain.Validation.UserValidator;
import Domain.Validation.Validator;
import Repository.AbstractRepositories.Repository;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This class provides basic operations for managing users.
 */
public class BasicUserService {
    protected final Repository<Long, User> repository;
    private final Validator<User> validator;
    private Long nextID;

    private void setNextID(){
        Optional<User> maxUser = repository.findAll(DefaultFilters::filterTrue).stream().max(Comparator.comparing(User::getId));
        if(maxUser.isPresent()) {
            nextID = maxUser.get().getId() + 1;
        }
        else{
            nextID = 1L;
        }
    }

    /**
     * This function creates a user service and sets the next available user ID.
     * @param repository the repository used to store and retrieve users;
     */
    public BasicUserService(Repository<Long, User> repository){
        this.repository = repository;
        this.validator = new UserValidator().andThen((user) -> {
            if(!user.getName().matches("[a-zA-Z -]+")){
                throw new ValidationException("The name must contain only letters!");
            }
            if(!user.getFirstName().matches("[a-zA-Z -]+")){
                throw new ValidationException("The first name must contain only letters!");
            }
        });
        nextID = 0L;
        setNextID();
    }

    /**
     * This function validates and adds a user using the next available ID.
     * The {@code email} value is stored as the user's first name.
     * @param name the user's name;
     * @param firstName the value used as the user's first name;
     * @throws ValidationException if either user name field is empty;
     * @throws RepositoryException if the user cannot be added to the repository;
     */
    public void addUser(String name, String firstName) {
        User new_user = new User(nextID,name,firstName);
        validator.validate(new_user);
        repository.addElement(new_user);
        nextID += 1;
    }

    /**
     * This function removes a user by ID.
     * @param id the ID of the user to remove;
     * @throws ValidationException if no user with the specified ID exists;
     */
    public void deleteUser(Long id) {
        if(repository.deleteElement(id).isEmpty()){
            throw new ValidationException("No user with this ID exists, so it can't be erased!");
        }
    }

    /**
     * This function returns the users where their full name contains the string given as parameter.
     * @param nameFragment a string which represents the searching criteria;
     * @return A list of users, where the users' name contains the given string.
     */
    public List<User> searchByName(String nameFragment){
        return repository.findAll(u -> u.getName().toLowerCase(Locale.ROOT).contains(nameFragment.toLowerCase(Locale.ROOT)) ||
                    u.getFirstName().toLowerCase(Locale.ROOT).contains(nameFragment.toLowerCase(Locale.ROOT))
        );
    }
}

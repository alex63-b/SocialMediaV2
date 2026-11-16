package Domain.Validation;

import Domain.Objects.User;

/**
 * This class validates users by checking their IDs, names, and first names.
 */
public class UserValidator implements Validator<User>{
    /**
     * This function checks that a user's ID is greater than zero.
     * @param id the ID to validate;
     * @throws ValidationException if the ID is zero or negative;
     */
    private void validateId(Long id) throws ValidationException{
        if(id <= 0){
            throw new ValidationException("The ID cannot be a negative number or zero!");
        }
    }

    /**
     * This function checks that a name is not empty.
     * @param name the name to validate;
     * @throws ValidationException if the name is empty;
     */
    private void validateName(String name) throws ValidationException{
        if(name.isEmpty()){
            throw new ValidationException("The name cannot be empty!");
        }
    }

    /**
     * This function validates a user's ID, name, and first name.
     * @param user the user to validate;
     * @throws ValidationException if one or more of the user's fields are invalid;
     */
    @Override
    public void validate(User user) throws ValidationException {
        StringBuilder error_builder = new StringBuilder();
        try{
            validateId(user.getId());
        }
        catch (ValidationException e){
            error_builder.append(e.getMessage());
            error_builder.append('\n');
        }
        try{
            validateName(user.getName());
        }
        catch (ValidationException e){
            error_builder.append(e.getMessage());
            error_builder.append('\n');
        }
        try{
            validateName(user.getFirstName());
        }
        catch (ValidationException e){
            error_builder.append(e.getMessage());
            error_builder.append('\n');
        }
        if(!error_builder.isEmpty()){
            throw new ValidationException(error_builder.toString());
        }
    }
}

package Domain.Validation;

import Domain.Objects.Friendship;

import java.time.LocalDateTime;

/**
 * This class validates friendships by checking their users and start dates.
 */
public class FriendshipValidator implements Validator<Friendship>{
    /**
     * This function checks that both users are valid and are not the same user.
     * @param friendship the friendship whose users to validate;
     * @throws ValidationException if either user is invalid or both users are the same;
     */
    private void validateFriends(Friendship friendship){
        UserValidator validator = new UserValidator();
        try {
            validator.validate(friendship.getFriend1());
        }
        catch (ValidationException e){
            throw new ValidationException("The first friend doesn't have valid fields!");
        }
        try {
            validator.validate(friendship.getFriend2());
        }
        catch (ValidationException e){
            throw new ValidationException("The second friend doesn't have valid fields!");
        }
        if(friendship.getFriend1().equals(friendship.getFriend2())){
            throw new ValidationException("You cannot form a friendship between a person and themselves!");
        }
    }

    /**
     * This function checks that the friendship's start date is not in the future.
     * @param friendship the friendship whose start date to validate;
     * @throws ValidationException if the start date is in the future;
     */
    private void validateDate(Friendship friendship){
        if(friendship.getDate().isAfter(LocalDateTime.now())){
            throw new ValidationException("The date on which the friendship started cannot be in the future!");
        }
    }

    /**
     * This function validates a friendship's users and start date.
     * @param entity the friendship to validate;
     * @throws ValidationException if one or more of the friendship's fields are invalid;
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        try{
            validateFriends(entity);
        }
        catch (ValidationException e){
            errors.append(e.getMessage());
            errors.append("\n");
        }
        try{
            validateDate(entity);
        }
        catch (ValidationException e){
            errors.append(e.getMessage());
            errors.append("\n");
        }
        if(!errors.isEmpty()){
            throw new ValidationException(errors.toString());
        }
    }
}

package Domain.Validation.IntegrityValidation;

import Domain.Objects.FriendRequest;
import Domain.Objects.FriendRequestStates;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest> {
    private void validateId(Long id){
        if(id <= 0){
            throw new ValidationException("The ID of the friend request can't be less than 1!");
        }
    }
    private void validateIdPair(Long from, Long to){
        if(from <= 0){
            throw new ValidationException("The ID of the friend that sent the request can't be less than 0!");
        }
        if(to <= 0){
            throw new ValidationException("The ID of the friend that received the request can't be less than 0!");
        }
    }
    private void validateStatus(FriendRequestStates status){
        if(status == null){
            throw new ValidationException("The status of the friend request can't be null!");
        }
    }
    private void validateDate(LocalDateTime date){
        if(date.isAfter(LocalDateTime.now())){
            throw new ValidationException("The friend request can't have the date in the future!");
        }
    }
    /**
     * This function checks whether an object satisfies its validation rules.
     *
     * @param entity the object to validate;
     * @throws ValidationException if the object does not satisfy its validation rules;
     */
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        StringBuilder errorBuilder = new StringBuilder();
        try{
            validateId(entity.getId());
        }
        catch (ValidationException e) {
            errorBuilder.append(e.getMessage());
        }
        try{
            validateIdPair(entity.getFrom(),entity.getTo());
        }
        catch (ValidationException e) {
            errorBuilder.append(e.getMessage());
        }
        try {
            validateStatus(entity.getStatus());
        }
        catch (ValidationException e) {
            errorBuilder.append(e.getMessage());
        }
        try{
            validateDate(entity.getDate());
        }
        catch (ValidationException e) {
            errorBuilder.append(e.getMessage());
        }
        if(!errorBuilder.isEmpty()){
            throw new ValidationException(errorBuilder.toString());
        }
    }
}

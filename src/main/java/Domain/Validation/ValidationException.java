package Domain.Validation;

/**
 * This exception indicates that an object does not satisfy its validation rules.
 */
public class ValidationException extends RuntimeException {
    /**
     * This function creates a validation exception with the specified message.
     * @param message the detail message describing the validation error;
     */
    public ValidationException(String message) {
        super(message);
    }
}

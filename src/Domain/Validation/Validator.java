package Domain.Validation;

import Domain.Objects.Entity;

/**
 * This interface defines validation for objects of a specified type.
 * @param <E> the type of the objects to validate;
 */
@FunctionalInterface
public interface Validator<E> {
    /**
     * This function checks whether an object satisfies its validation rules.
     * @param entity the object to validate;
     * @throws ValidationException if the object does not satisfy its validation rules;
     */
    void validate(E entity) throws ValidationException;

    /**
     * This function chains a validation rule with another validation rule through a functional interface, and returns the validator
     * that checks the chained validation rule.
     * @param other another validator, its validation condition will be put after the validation condition of the current validator;
     * @return A validator where the validation condition is just the two validator conditions chained one after another.
     */
    default Validator<E> andThen(Validator<E> other){
        return (entity) -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}

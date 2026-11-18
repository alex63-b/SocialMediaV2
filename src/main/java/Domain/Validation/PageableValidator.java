package Domain.Validation;

import util.paging.Pageable;

/**
 * Validates pagination arguments before they are used by repositories or services.
 */
public class PageableValidator implements Validator<Pageable> {
    /**
     * This function checks whether the pageable satisfies its validation rules.
     *
     * @param entity the pageable to validate;
     * @throws IllegalArgumentException if the page size is 0 or less, or if the page number is negative;
     */
    @Override
    public void validate(Pageable entity) {
        if(entity.getPageNumber() < 0){
            throw new IllegalArgumentException("The page number can't be negative!");
        }
        if(entity.getPageSize() <= 0){
            throw new IllegalArgumentException("The page size can't be below 1!");
        }
    }
}

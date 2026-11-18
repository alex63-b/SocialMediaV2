package Repository.AbstractRepositories;

import Domain.Objects.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Defines repository operations that retrieve entities in pages.
 *
 * @param <ID> the type of an entity identifier
 * @param <E>  the entity type
 */
public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    /**
     * This function returns the wanted page based on the data from the pageable object(page number and page size)
     * @param pageable an object which remembers the page count and the page size;
     * @return A page object, which contains the objects from the repository that are in the desired page.
     */
    Page<E> findAllOnPage(Pageable pageable);
}

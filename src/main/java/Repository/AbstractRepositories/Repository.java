package Repository.AbstractRepositories;

import Domain.Objects.Entity;
import Domain.Validation.ValidationException;
import Repository.Exceptions.RepositoryException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This interface defines operations for storing and retrieving entities.
 * @param <ID> the type of the entities' identifiers;
 * @param <E> the type of entities stored in the repository;
 */
public interface Repository<ID,E extends Entity<ID>>{
    /**
     * This function returns the number of entities in the repository.
     * @return the number of stored entities;
     */
    int size();

    /**
     * This function returns a list of the entities in the repository that are accepted by the filter.
     * @param filter a predicate which acts as a filter for the entities;
     * @return A list of the filtered entities in the repository.
     */
    List<E> findAll(Predicate<E> filter);

    /**
     * This function finds an entity by its identifier.
     * @param id the identifier of the entity to find;
     * @return the entity with the specified identifier, or Optional.empty() if it doesn't exist in the repository;
     * @throws RepositoryException if the ID is null;
     */
    Optional<E> findElement(ID id);

    /**
     * This function validates and adds an entity to the repository.
     * @param new_element the entity to add;
     * @return Optional.empty() if an entity with the same identifier already exists, and it returns the entity if the add operation is successful;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     * @throws RepositoryException if the entity is null, or its ID is null;
     */
    Optional<E> addElement(E new_element);

    /**
     * This function removes an entity from the repository by its identifier.
     * @param id the identifier of the entity to remove;
     * @return Optional.empty() if no entity with the specified identifier exists, or the deleted element if the identifier exists;
     */
    Optional<E> deleteElement(ID id);

    /**
     * This function validates and replaces an existing entity.
     * @param new_element the entity containing the replacement data;
     * @return the given element if the element's id doesn't exist in the repository, and it returns Optional.empty() on success;
     * @throws RepositoryException if the entity is null;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     */
    Optional<E> modifyElement(E new_element);
}

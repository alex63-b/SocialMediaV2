package Repository.AbstractRepositories;

import Domain.Objects.Entity;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.Exceptions.RepositoryException;

import java.io.InvalidObjectException;
import java.security.KeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Stores entities in memory, indexed by their identifiers.
 *
 * @param <ID> the type of the entities' identifiers
 * @param <E> the type of entities stored in the repository
 */
public class MemoryRepository<ID,E extends Entity<ID>> implements Repository<ID,E>{
    private final Map<ID,E> repoMap;

    /** Validator used to check entities before they are stored. */
    protected final Validator<E> validator;

    /**
     * Creates an in-memory repository using the specified validator.
     *
     * @param validator the validator used to check entities before they are stored
     */
    public MemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.repoMap = new HashMap<>();
    }
    /**
     * Returns the number of entities in the repository.
     *
     * @return the number of stored entities
     */
    @Override
    public int size(){
        return repoMap.size();
    }

    /**
     * Returns the entities in the repository that satisfy the filter.
     *
     * @param filter the predicate used to filter entities
     * @return a list of entities accepted by the filter
     */
    @Override
    public List<E> findAll(Predicate<E> filter) {
        return this.repoMap.values().stream()
                .filter(filter)
                .toList();
    }
    /**
     * Finds an entity by its identifier.
     *
     * @param id the identifier of the entity to find
     * @return the entity with the specified identifier, or {@code Optional.empty()} if it is not in the repository
     * @throws RepositoryException if {@code id} is {@code null}
     */
    @Override
    public Optional<E> findElement(ID id) throws RepositoryException {
        if(id == null){
            throw new RepositoryException("The ID can't be null!",new InvalidObjectException(""));
        }
        if(repoMap.containsKey(id)){
            return Optional.of(this.repoMap.get(id));
        }
        return Optional.empty();
    }
    /**
     * Validates and adds an entity to the repository.
     *
     * @param new_element the entity to add
     * @return {@code Optional.empty()} if an entity with the same identifier already exists, or the added entity
     * @throws ValidationException if the entity does not satisfy its validation rules
     * @throws RepositoryException if the entity or its identifier is {@code null}
     */
    @Override
    public Optional<E> addElement(E new_element) throws RepositoryException, ValidationException {
        if(new_element == null){
            throw new RepositoryException("The new element can't be null!",new InvalidObjectException(""));
        }
        if(new_element.getId() == null){
            throw new RepositoryException("The new element's ID can't be null!",new InvalidObjectException(""));
        }
        validator.validate(new_element);
        if(repoMap.containsKey(new_element.getId())){
            return Optional.empty();
        }
        repoMap.put(new_element.getId(),new_element);
        return Optional.of(new_element);
    }
    /**
     * Removes an entity from the repository by its identifier.
     *
     * @param id the identifier of the entity to remove
     * @return {@code Optional.empty()} if no entity with the identifier exists, or the removed entity
     */
    @Override
    public Optional<E> deleteElement(ID id) throws RepositoryException {
        return Optional.ofNullable(repoMap.remove(id));
    }

    /**
     * Validates and replaces an existing entity.
     *
     * @param new_element the entity containing the replacement data
     * @return the given entity if its identifier is not in the repository, or {@code Optional.empty()} if it was replaced
     * @throws RepositoryException if the entity or its identifier is {@code null}
     * @throws ValidationException if the entity does not satisfy its validation rules
     */
    @Override
    public Optional<E> modifyElement(E new_element) throws RepositoryException {
        if(new_element == null){
            throw new RepositoryException("The entity can't be null!", new InvalidObjectException(""));
        }
        if(new_element.getId() == null){
            throw new RepositoryException("The entity's ID can't be null!", new InvalidObjectException(""));
        }
        validator.validate(new_element);
        if(!repoMap.containsKey(new_element.getId())){
            return Optional.of(new_element);
        }
        repoMap.replace(new_element.getId(),new_element);
        return Optional.empty();
    }
}

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
 * This class stores entities in memory, indexed by their identifiers.
 * @param <ID> the type of the entities' identifiers;
 * @param <E> the type of entities stored in the repository;
 */
public class MemoryRepository<ID,E extends Entity<ID>> implements Repository<ID,E>{
    private final Map<ID,E> repoMap;
    protected final Validator<E> validator;

    /**
     * This function creates an in-memory repository using the specified validator.
     * @param validator the validator used to check entities before they are stored;
     */
    public MemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.repoMap = new HashMap<>();
    }
    /**
     * This function returns the number of entities in the repository.
     * @return the number of stored entities;
     */
    @Override
    public int size(){
        return repoMap.size();
    }

    /**
     * This function returns a list of the entities in the repository that are accepted by the filter.
     * @param filter a predicate which acts as a filter for the entities;
     * @return A list of the filtered entities in the repository.
     */
    @Override
    public List<E> findAll(Predicate<E> filter) {
        return this.repoMap.values().stream()
                .filter(filter)
                .toList();
    }
    /**
     * This function finds an entity by its identifier.
     * @param id the identifier of the entity to find;
     * @return the entity with the specified identifier, or Optional.empty() if it doesn't exist in the repository;
     * @throws RepositoryException if the ID is null;
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
     * This function validates and adds an entity to the repository.
     * @param new_element the entity to add;
     * @return Optional.empty() if an entity with the same identifier already exists, and it returns the entity if the add operation is successful;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     * @throws RepositoryException if the entity is null, or its ID is null;
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
     * This function removes an entity from the repository by its identifier.
     * @param id the identifier of the entity to remove;
     * @return Optional.empty() if no entity with the specified identifier exists, or the deleted element if the identifier exists;
     */
    @Override
    public Optional<E> deleteElement(ID id) throws RepositoryException {
        return Optional.ofNullable(repoMap.remove(id));
    }

    /**
     * This function validates and replaces an existing entity.
     * @param new_element the entity containing the replacement data;
     * @return the given element if the element's id doesn't exist in the repository, and it returns Optional.empty() on success;
     * @throws RepositoryException if the entity is null;
     * @throws ValidationException if the entity does not satisfy its validation rules;
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

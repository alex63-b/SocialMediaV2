package Repository.AbstractRepositories;

import Domain.Objects.Entity;
import Domain.Validation.ValidationException;
import Domain.Validation.Validator;
import Repository.Exceptions.RepositoryException;
import Repository.Helpers.DefaultFilters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * This class stores entities in a file and provides repository operations for them.
 * @param <ID> the type of the entities' identifiers;
 * @param <E> the type of entities stored in the repository;
 */
public abstract class FileRepository<ID, E extends Entity<ID>> extends MemoryRepository<ID, E>{
    String fileName;

    /**
     * This function creates a file-backed repository and loads its entities from the specified file.
     * @param validator the validator used to check loaded and stored entities;
     * @param fileName the path to the file used by the repository;
     * @throws RepositoryException if the file cannot be created or opened;
     * @throws ValidationException if an entity cannot be validated;
     */
    public FileRepository(Validator<E> validator, String fileName){
        super(validator);
        this.fileName = fileName;
        load();
    }

    /**
     * This function converts a line from the repository file into an entity.
     * @param line the line to convert;
     * @return the entity represented by the line;
     * @throws RepositoryException if the line cannot be parsed as an entity;
     * @throws ValidationException if the parsed entity is invalid;
     */
    abstract protected E lineToEntity(String line);

    /**
     * This function converts an entity into a line for the repository file.
     * @param entity the entity to convert;
     * @return the line representing the entity;
     */
    abstract protected String entityToLine(E entity);

    /**
     * This function loads and validates entities from the repository file.
     * Invalid or duplicate entries are reported and skipped.
     * @throws RepositoryException if the file cannot be created or opened;
     */
    protected void load() throws RepositoryException, ValidationException {
        try(var lines = Files.lines(Path.of(fileName))){
            List<String> filteredLines = lines
                    .filter(DefaultFilters::filterNotEmptyLines)
                    .toList();
            IntStream.range(0,filteredLines.size())
                    .forEach((n) -> {
                        try {
                            E new_entity = lineToEntity(filteredLines.get(n));
                            validator.validate(new_entity);
                            noStoreAddElement(new_entity);
                        }
                        catch (ValidationException | RepositoryException e) {
                            System.out.println("Line " + n + ": " + e.getMessage());
                        }
                    });
            filteredLines
                    .forEach((line) -> {
                        try {
                            E new_entity = lineToEntity(line);
                            validator.validate(new_entity);
                            noStoreAddElement(new_entity);
                        }
                        catch (ValidationException | RepositoryException _) {

                        }
                    });
        }
        catch (IOException e){
            try {
                File newFile = new File(fileName);
                if(!newFile.createNewFile()){
                    throw new RepositoryException("The file already exists but couldn't be opened!",e.getCause());
                }
            }
            catch (IOException ex) {
                throw new RepositoryException("The file couldn't be created!",ex.getCause());
            }
        }
    }

    private void store() throws RepositoryException, ValidationException{
        findAll(DefaultFilters::filterTrue).forEach(validator::validate);
        List<String> lines = findAll(DefaultFilters::filterTrue).stream()
                .map(this::entityToLine)
                .toList();
        try{
            Files.write(Path.of(fileName),lines);
        }
        catch (IOException e) {
            throw new RepositoryException("The file couldn't be opened for writing!",e.getCause());
        }
    }

    /**
     * This function adds a validated entity and saves the repository to its file.
     * @param new_element the entity to add;
     * @throws RepositoryException if an entity with the same identifier exists or the file cannot be written;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     */
    @Override
    public Optional<E> addElement(E new_element) throws RepositoryException, ValidationException {
        Optional<E> result = super.addElement(new_element);
        store();
        return result;
    }

    /**
     * This function removes an entity and saves the repository to its file.
     * @param id the identifier of the entity to remove;
     * @throws RepositoryException if the entity does not exist or the file cannot be written;
     */
    @Override
    public Optional<E> deleteElement(ID id) throws RepositoryException {
        Optional<E> result = super.deleteElement(id);
        store();
        return result;
    }

    /**
     * This function replaces an entity and saves the repository to its file.
     * @param new_element the entity containing the replacement data;
     * @throws RepositoryException if the entity does not exist or the file cannot be written;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     */
    @Override
    public Optional<E> modifyElement(E new_element) throws RepositoryException {
        Optional<E> result = super.modifyElement(new_element);
        store();
        return result;
    }

    /**
     * This function adds a validated entity without immediately saving the repository to its file.
     * @param new_element the entity to add;
     * @throws RepositoryException if an entity with the same identifier already exists;
     * @throws ValidationException if the entity does not satisfy its validation rules;
     */
    protected Optional<E> noStoreAddElement(E new_element) throws RepositoryException, ValidationException{
        return super.addElement(new_element);
    }
}

package Repository.Exceptions;

/**
 * This exception indicates that a repository operation could not be completed.
 */
public class RepositoryException extends RuntimeException {
    /**
     * This function creates a repository exception with a message and cause.
     * @param message the detail message describing the repository error;
     * @param cause the cause of the repository error;
     */
    public RepositoryException(String message, Throwable cause) {
        super(message,cause);
    }
}

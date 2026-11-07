package Repository.Exceptions;

/**
 * This exception indicates that repository configuration is missing or invalid.
 */
public class ConfigException extends RuntimeException {
    /**
     * This function creates a configuration exception with the specified message.
     * @param message the detail message describing the configuration error;
     */
    public ConfigException(String message) {
        super(message);
    }

}

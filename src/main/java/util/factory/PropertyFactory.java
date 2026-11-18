package util.factory;

import Repository.Exceptions.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * This class loads repository configuration properties from a file.
 */
public class PropertyFactory {
    private static PropertyFactory propertyFactory = null;
    private PropertyFactory(){}

    /**
     * This function gets the current property factory that was created previously, or a new property factory if this is the first call of the function.
     * @return A property factory, that can generate properties.
     */
    public static PropertyFactory getPropertyFactory(){
        if(propertyFactory == null){
            propertyFactory = new PropertyFactory();
        }
        return propertyFactory;
    }
    /**
     * This function reads properties from the specified configuration file.
     * @param filePath the path to a {@code .properties} configuration file;
     * @return the properties loaded from the file;
     * @throws ConfigException if the path is blank, has the wrong extension, or the file cannot be read;
     */
    public Properties propertiesFromConfigFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new ConfigException("The config file path cannot be blank!");
        }
        if (!filePath.endsWith(".properties")) {
            throw new ConfigException("The config file must have a .properties extension!");
        }

        Properties properties = new Properties();

        try (InputStream configStream = Files.newInputStream(Path.of(filePath))) {
            properties.load(configStream);
            return properties;
        }
        catch (IOException e) {
            throw new ConfigException("The config file could not be read: " + filePath);
        }
    }
}

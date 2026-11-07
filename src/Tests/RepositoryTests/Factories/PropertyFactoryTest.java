package Tests.RepositoryTests.Factories;

import Repository.Exceptions.ConfigException;
import Repository.Factory.PropertyFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyFactoryTest{
    private static final String config_path = "test_data/Resources/test_config.properties";
    @Test
    public void testPropertiesFromConfigFile(){
        PropertyFactory propertyFactory = new PropertyFactory();
        assertThrows(ConfigException.class,() -> propertyFactory.propertiesFromConfigFile(""));
        assertThrows(ConfigException.class,() -> propertyFactory.propertiesFromConfigFile("test_data/Friendships/friendship_data.csv"));

        assertEquals("FILE",propertyFactory.propertiesFromConfigFile(config_path).getProperty("repository.type"));
    }
}

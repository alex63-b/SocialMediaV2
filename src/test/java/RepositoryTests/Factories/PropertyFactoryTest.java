package RepositoryTests.Factories;

import Repository.Exceptions.ConfigException;
import util.factory.PropertyFactory;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyFactoryTest{
    private static final String config_path = "test_data/Resources/test_config.properties";
    @Test
    public void testPropertiesFromConfigFile(){
        PropertyFactory propertyFactory = PropertyFactory.getPropertyFactory();
        assertThrows(ConfigException.class,() -> propertyFactory.propertiesFromConfigFile(""));
        assertThrows(ConfigException.class,() -> propertyFactory.propertiesFromConfigFile("test_data/Friendships/friendship_data.csv"));

        Properties properties = propertyFactory.propertiesFromConfigFile(config_path);
        assertEquals("FILE",properties.getProperty("repository.type"));
        assertEquals(4,Integer.parseInt(properties.getProperty("db.pageSize")));
    }
}

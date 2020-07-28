package java8.play2.example.utilities;

import play.libs.Json;

import org.junit.Test;
import junit.framework.Assert;
import com.fasterxml.jackson.databind.JsonNode;

public class ExampleEnvConfigurationTest {
    private ExampleEnvConfiguration DefaultExampleEnvConfiguration = new ExampleEnvConfiguration();

    private String jsonConf = "{\n" +
            "    \"some_example_stuff\": {\n" +
            "        \"some_example_element\": {\n" +
            "            \"some_example-element2\": \"some-example-value\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"env\": \"integration\",\n" +
            "}";

    private String jsonConfWithEmptySomeExampleStuff = "{\n" +
            "    \"some_example_stuff\": {},\n" +
            "    \"env\": \"integration\",\n" +
            "}";

    private String jsonConfWithoutSomeExampleStuff = "{\n" +
            "    \"env\": \"integration\",\n" +
            "}";

    private JsonNode jsonNode;

    @Test
    public void testSetEnvironmentProperties() {
        jsonNode = Json.parse(jsonConf.getBytes());
        DefaultExampleEnvConfiguration.defaultExampleSetEnvProps(jsonNode);
        Assert.assertEquals(System.getProperty("env"), "integration");
    }

    @Test
    public void testSetEnvironmentPropertiesWhenNoConfig() {
        jsonNode = Json.parse(jsonConfWithoutSomeExampleStuff.getBytes());
        DefaultExampleEnvConfiguration.defaultExampleSetEnvProps(jsonNode);
        Assert.assertNull(System.getProperty("some.example.stuff.property"));
    }

    @Test
    public void testSetEnvironmentPropertiesWhenEmptyConfig() {
        jsonNode = Json.parse(jsonConfWithEmptySomeExampleStuff.getBytes());
        DefaultExampleEnvConfiguration.defaultExampleSetEnvProps(jsonNode);
        Assert.assertNull(System.getProperty("some.example.element.property"));
    }
}

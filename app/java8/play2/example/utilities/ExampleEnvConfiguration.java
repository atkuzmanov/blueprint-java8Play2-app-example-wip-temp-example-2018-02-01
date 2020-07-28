package java8.play2.example.utilities;

import java8.play2.example.aws.ExampleDynamoDbDaoLayer;
import java8.play2.example.aws.dummies.DummyDefaultDynamoDbDaoLayer;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

import com.amazonaws.util.StringUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Iterator;

import java.nio.file.Paths;
import java.nio.file.Files;

import javax.inject.Singleton;

@Singleton
public class ExampleEnvConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExampleEnvConfiguration.class);

    private static String defaultExampleServerConfPath = "/etc/default/exampleJSONConfigurationFile.json";

    public static int exampleStubServerPort = 3333;
    public static String exampleStubServerHost = "http://localhost";

    private static JsonNode getDeployedConfigJson() {
        JsonNode configfile = null;
        try {
            configfile = Json.parse(Files.readAllBytes(Paths.get(defaultExampleServerConfPath)));
        } catch (Exception e) {
            LOGGER.error("Unable to readNumberOfSQSMsgs the cosmos config file.", e);
        }
        return configfile;
    }

    public static String systemRunningEnvironment() {
        String runningEnvironment = System.getenv("DEFAULT_SYSTEM_ENV");
        LOGGER.info("System running environment: " + runningEnvironment);
        System.out.println("System running environment: " + runningEnvironment);
        if (StringUtils.isNullOrEmpty(runningEnvironment)) {

            boolean isDefaultServerConfReadable = Files.isReadable(Paths.get(defaultExampleServerConfPath));
            LOGGER.info("Default server configuration file located at " + defaultExampleServerConfPath + " exists and is readable: " + isDefaultServerConfReadable);

            JsonNode defaultConfigAsJSON;
            try {defaultConfigAsJSON = Json.parse(Files.readAllBytes(Paths.get(defaultExampleServerConfPath)));
                if (isDefaultServerConfReadable && defaultConfigAsJSON != null) {
                    defaultExampleSetEnvProps(defaultConfigAsJSON);
                    return defaultConfigAsJSON.get("env").textValue();
                } else {
                    throw new Exception("Default server configuration file  " + defaultExampleServerConfPath + "exists but value for the env key could not be found.");
                }
            } catch (Exception ex) {
                LOGGER.error("Not able to read server configuration file.", ex);
            }
        }
        return runningEnvironment;
    }

    public static void defaultExampleSetEnvProps(JsonNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> defaultJsonConfigFields = null;
        try {
            defaultJsonConfigFields = jsonNode.get("default_example_config").fields();
            LOGGER.info("Processing and setting system configuration...");
            while (defaultJsonConfigFields.hasNext()) {
                Map.Entry<String, JsonNode> configEntry = defaultJsonConfigFields.next();
                String configKey = configEntry.getKey();
                JsonNode nodeValue = configEntry.getValue();
                String configValue = nodeValue.textValue();
                System.setProperty(configKey, configValue);
            }
        } catch (Exception ex) {
            LOGGER.warn("Exceptional behaviour encoutered.", ex);
            System.out.println("Exceptional behaviour encoutered.");
        }
    }

    public static void setEnvironmentVariables() {
        ExampleEnvInit environmentSetup = new ExampleEnvInit(systemRunningEnvironment());
    }

    // Validate for null or empty.
    private String validateForNullOrEmptyAndGet(String keyName) {
        String keyValue = System.getProperty(keyName);
        if (org.apache.commons.lang.StringUtils.isEmpty(keyValue)) {
            LOGGER.error("Key " + keyName + " is null or empty.");
            throw new IllegalArgumentException("Key " + keyName + " is null or empty.");
        }
        return keyValue.trim();
    }

    public static ExampleDynamoDbDaoLayer getDynamoClient() {
        String env = ExampleEnvConfiguration.systemRunningEnvironment();
        if (env.equals("dev") || env.equals("mgmt")) {
            return new DummyDefaultDynamoDbDaoLayer();
        } else {
            return new ExampleDynamoDbDaoLayer();
        }
    }

    public static String getDefaultMongoDBConnectionUrl() {
        return "mongodb://localhost:27017";
    }
}

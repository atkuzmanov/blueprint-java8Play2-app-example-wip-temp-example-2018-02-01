package java8.play2.example.utilities;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ExampleEnvInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleEnvInit.class);
    public ExampleEnvInit(String environment) {
        LOGGER.info("Initialising environment.");
        switch (environment) {
            case "dev":
                System.clearProperty("some.default.example.property.name");
                System.getProperties().clear();
                LOGGER.info("Development, non-production, non-live environment.");
                System.setProperty("DEFAULT_SYSTEM_ENV", environment);
                System.setProperty("javax.net.ssl.trustStore", "/etc/pki/exampleTruststore.jks");
                System.setProperty("javax.net.ssl.keyStore", "/etc/pki/exampleCertificate.p12");
                System.setProperty("javax.net.ssl.keyStorePassword", "defaultExamplePassword");
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                break;
            default:
                LOGGER.info("System execution environment: " + environment);
                System.setProperty("DEFAULT_SYSTEM_ENV", environment);
                System.setProperty("javax.net.ssl.trustStore", "/etc/pki/exampleTruststore.jks");
                System.setProperty("javax.net.ssl.keyStore", "/etc/pki/exampleCertificate.p12");
                System.setProperty("javax.net.ssl.keyStorePassword", "defaultExamplePassword");
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                System.setProperty("javax.net.ssl.keyStoreLocation", "/etc/pki/tls/private/client.p12");
                System.setProperty("play.modules.enabled", "modules.ExampleStartupConfigModule");
        }
    }
}

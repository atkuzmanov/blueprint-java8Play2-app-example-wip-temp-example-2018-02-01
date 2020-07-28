package java8.play2.example.aws;

import javax.inject.Singleton;

import java8.play2.example.utilities.ExampleEnvConfiguration;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.ClientConfiguration;

@Singleton
public class ExampleAwsCredentialsConfigurator {

    private final String environmentString;
    private final ExampleEnvConfiguration envConfig = new ExampleEnvConfiguration();

    protected final AWSCredentialsProvider awsCredentialsProvider;
    protected final ClientConfiguration awsClientConfiguration;

    protected String getEnvironment() {
        return environmentString;
    }

    protected ExampleAwsCredentialsConfigurator() {
        this.environmentString = envConfig.systemRunningEnvironment();

        if (environmentString.equals("dev")) {
            this.awsCredentialsProvider = new EnvironmentVariableCredentialsProvider();
            this.awsClientConfiguration = new ClientConfiguration()
                    .withProxyHost("www.defaultexampleproxyurl.com")
                    .withProxyPort(8080);
        } else {
            this.awsCredentialsProvider = new InstanceProfileCredentialsProvider();
            this.awsClientConfiguration = new ClientConfiguration();
        }
    }
}

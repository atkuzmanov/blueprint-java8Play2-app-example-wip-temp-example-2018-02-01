package java8.play2.example.monitoring;

import java8.play2.example.utilities.ExampleEnvConfiguration;

import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.lang.StringUtils;

public class ExampleJavaStatsD {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleJavaStatsD.class);

    private static final StatsDClient statsD = new NonBlockingStatsDClient("example-prefix", "example-host-name", 8080);

    private final String systemEnv;

    public ExampleJavaStatsD() {
        this.systemEnv = ExampleEnvConfiguration.systemRunningEnvironment();
    }

    private synchronized void incrementCounter(String counter) {
        statsD.incrementCounter(getEnv() + "." + counter);
    }

    private synchronized String getEnv() {
        String env = systemEnv;
        try {
            if (!StringUtils.isBlank(env)) {
                env = env.toLowerCase();
            } else {
                LOGGER.error("Unable to get current system environment and all stats will be send to development env.");
                env = "development";
            }
            if (env.matches("integration") || env.matches("testing") || env.matches("staging") || env.matches("production")) {
                return env;
            }
        } catch (Exception e) {
            LOGGER.error("Exceptional state encoutnered when trying to get the system environment.", e);
        }
        return env;
    }

    //___

    public synchronized void exampleRecordDynamoDBTableScanTime(long timeInMilliseconds) {
        statsD.time(getEnv() + ".example.dynamoDB.scantime", timeInMilliseconds);
    }

    public synchronized void exampleIncrementDynamoDBSingleReadCount() {
        incrementCounter("example.dynamodb.read.single");
    }

    public synchronized void exampleIncrementDynamoDBReadErrorCount() {
        incrementCounter("example.dynamodb.read.error");
    }

    public synchronized void exampleIncrementDynamoDBReadProvisionCount() {
        incrementCounter("example.dynamodb.read.provision");
    }

    public synchronized void exampleIncrementDynamoDBReadSinceCount() {
        incrementCounter("example.dynamodb.read.sinceLast5DaysIfEpochNotProvided");
    }

    public synchronized void exampleIncrementDynamoDBWriteProvisionCount() {
        incrementCounter("example.dynamoDB.write.provision");
    }

    //___

    public synchronized void exampleIncrementDynamoDBWriteCount() {
        incrementCounter("example.dynamodb.write");
    }

    public synchronized void exampleIncrementDynamoDBWriteErrorCount() {
        incrementCounter("example.dynamodb.write.error");
    }

    public synchronized void exampleIncrementMongoRead() {
        incrementCounter("example.mongodb.read");
    }


    public synchronized void exampleIncrementMongoWrite() {
        incrementCounter("example.mongodb.write");
    }

    public synchronized void exampleIncrementMongoError(String methodName) {
        incrementCounter("example.mongodb.error." + methodName);
    }

    //___

    public synchronized void exampleIncrementDefaultErrorCounter() {
        incrementCounter("exampl.default.counter.error");
    }

    //___

    public synchronized void exampleIncrementHttpCallErrorCount(int httpStatusErrorCode) {
        incrementCounter("example.http.error.status.code." + Integer.toString(httpStatusErrorCode));
    }

    public synchronized void exampleIncrementApiEndpointUserAgentCount(String userAgent) {
        if (!StringUtils.isBlank(userAgent)) {
            incrementCounter("example.api.endpoint.userAgent." + userAgent.replaceAll("\\.", "_"));
        } else {
            incrementCounter("example.api.endpoint.userAgent" + ".unknown");
        }
    }
}

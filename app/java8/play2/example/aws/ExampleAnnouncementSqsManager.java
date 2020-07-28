package java8.play2.example.aws;

import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.domain.ExampleContentObj1;
import java8.play2.example.services.ExampleMongoDBService;
import java8.play2.example.services.ExampleSomeProcessingService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.services.sqs.model.Message;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.TimerTask;

public class ExampleAnnouncementSqsManager extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleAnnouncementSqsManager.class);

    private final Config CONFIG = ConfigFactory.load();

    private static final String NUMBER_OF_MAXIMUM_MSGS_TO_READ_KEY = "NUMBER_OF_MAXIMUM_MSGS_TO_READ_KEY_FROM_APPLICATION_CONF";
    private static final int DEFAULT_NUMBER_OF_MESSAGES_TO_READ = 1;
    private static final long MAXIMUM_AGE_OF_MSG_IN_MILLISECONDS = 5 * 60 * 1000;
    private static final String NAME_OF_QUEUE = "-default-example-queue-name";

    private final ExampleSomeProcessingService someProcessingService;

    private final ExampleMongoDBService exampleMongoDBService;

    private final ExampleSqsMessageAssistant ExampleSqsMessageAssistant;

    @Inject
    public ExampleAnnouncementSqsManager(ExampleSqsMessageAssistant ExampleSqsMessageAssistant, ExampleSomeProcessingService someProcessingService, ExampleMongoDBService exampleMongoDBService) {
        this.someProcessingService = someProcessingService;
        this.exampleMongoDBService = exampleMongoDBService;
        this.ExampleSqsMessageAssistant = ExampleSqsMessageAssistant;
    }

    private int getNumberOfMessagesToRead() {
        try {
            int numberOfMsgFromConfig = CONFIG.getInt(NUMBER_OF_MAXIMUM_MSGS_TO_READ_KEY);
            if (numberOfMsgFromConfig != 0) {
                return numberOfMsgFromConfig;
            }
        } catch (ConfigException configurationException) {
            LOGGER.warn("Did not find configuration key >>> " + NUMBER_OF_MAXIMUM_MSGS_TO_READ_KEY, configurationException.getMessage());
        }
        return DEFAULT_NUMBER_OF_MESSAGES_TO_READ;
    }

    private boolean isMsgNewerThanAnHourAgo(ExampleContentObj1 payload) {
        long currentTimeMillis = System.currentTimeMillis();
        boolean result = currentTimeMillis - payload.getTimeStamp() < MAXIMUM_AGE_OF_MSG_IN_MILLISECONDS;
        LOGGER.info("Checking if payload timestamp [" + payload.getTimeStamp() + "] is newer than an hour ago.  Current time [" + currentTimeMillis + "]. Result =  " + result);
        return result;
    }

    @Override
    public void run() {
        try {
            List<Message> sqsMessages = ExampleSqsMessageAssistant.defaultReadMessages(getNumberOfMessagesToRead(), NAME_OF_QUEUE);
            if (!sqsMessages.isEmpty()) {
                sqsMessages.forEach(message -> {
                    JsonNode msgBodyAsJsonString = play.libs.Json.parse(message.getBody());

                    if (msgBodyAsJsonString.get("isFake").toString().equalsIgnoreCase("itsfake")) {
                        LOGGER.error("Throwing exception as message is fake.");
                        throw new UnsupportedOperationException("Runtime exception in ExampleAnnouncementSqsManager.");
                    }

                    ExampleContentObj1 defaultExampleContentObj1 = new ExampleContentObj1();
                    ExampleContentObj2 someExampleContentObj1 = new ExampleContentObj2();

                    if (isMsgNewerThanAnHourAgo(defaultExampleContentObj1)) {
                        // Do some processing ...
                        someProcessingService.someExampleProcessingMethod(someExampleContentObj1);
                    } else {
                        LOGGER.warn("Ignoring Client SQS Message because it is too old to process");
                    }
                });
                ExampleSqsMessageAssistant.defaultDeleteMessages(sqsMessages, NAME_OF_QUEUE);
            }
        } catch (Throwable t) {
            // Catch any Throwable and print it out, but don't break - otherwise the Timer will not re-run this task.
            LOGGER.error("Throwable caught when processing in ExampleAnnouncementSqsManager", t);
        }
    }
}

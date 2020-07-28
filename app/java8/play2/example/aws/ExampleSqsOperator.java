package java8.play2.example.aws;

import java8.play2.example.factory.ExampleExecuterService;
import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.domain.aws.ExampleMsgContentObj;
import java8.play2.example.parsers.ExampleAwsMsgAssistant;
import java8.play2.example.services.ExampleMongoDBService;
import java8.play2.example.services.ExampleServiceApi;
import java8.play2.example.services.ExampleSomeProcessingService;

import com.google.inject.Inject;

import com.amazonaws.services.sqs.model.Message;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigException;
import com.typesafe.config.Config;

public class ExampleSqsOperator extends TimerTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSqsOperator.class);

    private static final int NUMBER_OF_MESSAGES_TO_PICK_UP = 1;
    private static final long MESSAGE_MAXIMUM_AGE_MILLISECONDS = 1000 * 60 * 5;
    private static final String NAME_OF_SQS = "-example-sqs-name";
    private static final String MAXIMUM_NUMBER_OF_MESSAGES = "default.example.app.conf.maxNumberOfMessages";

    private final Predicate<ExampleMsgContentObj> predicate1 = this::verifyTimestamp;
    private final Predicate<ExampleMsgContentObj> predicate2 = p -> Arrays.asList("SOME-EXAMPLE-VALUE").contains("some example value");

    private final ExampleSqsMessageAssistant exampleSqsMessageAssistant;
    private final ExampleExecuterService defaultExampleExecuterService;
    private final ExampleDynamoDbDaoLayer defaultDynamoDbDaoLayer;
    private final ExampleServiceApi exampleServiceApi;
    private final ExampleSomeProcessingService processingService;

    private final ExampleMongoDBService mongoService;

    private final Config config = ConfigFactory.load();

    private boolean verifyTimestamp(ExampleMsgContentObj mesgContObj){
        long currentTime = System.currentTimeMillis();
        boolean isMessageNewer = currentTime - (mesgContObj.getDefaultTimeStamp()*1000) < MESSAGE_MAXIMUM_AGE_MILLISECONDS;
        LOGGER.debug("Verifying message.getDefaultTimeStamp (" + (mesgContObj.getDefaultTimeStamp()+1000) + ") is newer than an hour ago.  Current time = " + currentTime + ", isMessageNewer =  " + isMessageNewer);
        return isMessageNewer;
    }

    @Inject
    public ExampleSqsOperator(ExampleDynamoDbDaoLayer defaultDynamoDbDaoLayer, ExampleSqsMessageAssistant exampleSqsMessageAssistant,
                              ExampleExecuterService defaultExampleExecuterService, ExampleServiceApi exampleServiceApi,
                              ExampleSomeProcessingService processingService,
                              ExampleMongoDBService mongoDBService) {
        this.exampleSqsMessageAssistant = exampleSqsMessageAssistant;
        this.defaultExampleExecuterService = defaultExampleExecuterService;
        this.defaultDynamoDbDaoLayer = defaultDynamoDbDaoLayer;
        this.mongoService = mongoDBService;
        this.exampleServiceApi = exampleServiceApi;
        this.processingService = processingService;
    }

    private int retrieveNumberOfMessagesToRead() {
        try {
            int numMsgsFromConf = config.getInt(MAXIMUM_NUMBER_OF_MESSAGES);
            if (numMsgsFromConf != 0) {
                return numMsgsFromConf;
            }
        } catch (ConfigException confEx) {
            LOGGER.warn("Configuration key not found: " + MAXIMUM_NUMBER_OF_MESSAGES, confEx.getMessage());
        }
        LOGGER.debug("Setting number of messages to default: " + NUMBER_OF_MESSAGES_TO_PICK_UP);
        return NUMBER_OF_MESSAGES_TO_PICK_UP;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Consuming messages from " + NAME_OF_SQS + " AWS SQS queue.");
            List<Message> consumedMessages = exampleSqsMessageAssistant.defaultReadMessages(retrieveNumberOfMessagesToRead(), NAME_OF_SQS);
            List<ExampleMsgContentObj> messagesFiltered = filterMessagesByPredicate(consumedMessages);
            forEachItemInStream(messagesFiltered);
            exampleFunctionJava8Mappings(messagesFiltered).stream().forEach(this::invokeServiceWithExecuter);
            exampleSqsMessageAssistant.defaultDeleteMessages(consumedMessages, NAME_OF_SQS);
        } catch (Throwable t) {
            // Catch any Throwable and log it out, don't break, otherwise the Timer will not re-execute this task.
            LOGGER.error("Throwable caught during execution in ExampleSqsOperator: ", t);
        }
    }

    private void invokeServiceWithExecuter(ExampleContentObj2 exampleContentObj1) {
        ExampleContentObj2 someExampleContentObj1 = defaultDynamoDbDaoLayer.retrieveItemDefault(exampleContentObj1.getSomeInt());
        defaultExampleExecuterService.process(someExampleContentObj1);
    }

    private List<ExampleContentObj2> exampleFunctionJava8Mappings(List<ExampleMsgContentObj> messages) {
        return messages.stream()
                .map(ExampleMsgContentObj::getSomeId)
                .peek(this::logSomething)
                .map(exampleServiceApi::createSomeExampleContentObj1)
                .collect(Collectors.toList());
    }

    private void forEachItemInStream(List<ExampleMsgContentObj> messages) {
        messages.stream().forEach(this::logSomething2);
    }

    private void logSomething(String stringToLog) {
        LOGGER.debug("Logging: " + stringToLog);
    }

    private void logSomething2(ExampleMsgContentObj defaultMsgContentObj) {
        LOGGER.debug("Logging: " + defaultMsgContentObj);
    }

    private List<ExampleMsgContentObj> filterMessagesByPredicate(List<Message> messages) {
        ExampleAwsMsgAssistant exampleMsgAssistant = new ExampleAwsMsgAssistant();
        return messages.stream()
                .map(exampleMsgAssistant::extractMsgContentObj)
                .filter(p -> p != null)
                .filter(predicate2)
                .filter(predicate1)
                .peek(this::logSomething2)
                .collect(Collectors.toList());
    }

    private Boolean isStringBlank(String jsonAsString) {
        return StringUtils.isBlank(jsonAsString);
    }
}

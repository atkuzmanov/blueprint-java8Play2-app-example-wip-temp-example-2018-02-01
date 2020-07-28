package java8.play2.example.aws;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton
public class ExampleSqsMessageAssistant {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleSqsMessageAssistant.class);

    private final ExampleSqsConsumptionClient defaultExampleSqsConsumptionClient;

    @Inject
    public ExampleSqsMessageAssistant(ExampleSqsConsumptionClient defaultExampleSqsConsumptionClient) {
        this.defaultExampleSqsConsumptionClient = defaultExampleSqsConsumptionClient;
    }

    public List<Message> defaultReadMessages(int numberOfMessagesToRead, String nameOfSqsQueue) {
        return defaultExampleSqsConsumptionClient.readNumberOfSQSMsgs(numberOfMessagesToRead, nameOfSqsQueue);
    }

    private void defaultDeleteMessage(Message message, String nameOfSqsQueue) {
        defaultExampleSqsConsumptionClient.deleteMessage(message, nameOfSqsQueue);
    }

    public void defaultDeleteMessages(List<Message> messages, String nameOfSqsQueue) {
        if (messages != null && nameOfSqsQueue != null) {
            LOG.info("Deleting number of messages: " + messages.size() + " from " +  nameOfSqsQueue + " message queue.");
            messages.forEach(msg -> this.defaultDeleteMessage(msg, nameOfSqsQueue));
        } else {
            throw new IllegalArgumentException("Required parameters not found - messages list and/or name of queue.");
        }
    }
}
package java8.play2.example.aws.dummies;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.services.sqs.model.*;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class DummyExampleAWSSQSClient extends AmazonSQSClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyExampleAWSSQSClient.class);

    private static List<Message> dummySQS1 = new ArrayList<>();
    private static List<Message> dummySQS2 = new ArrayList<>();

    @Override
    public ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
        String queueName = receiveMessageRequest.getQueueUrl();
        LOGGER.info(">>> Example AWS Dummy SQS Client receiveMessage(...) called for queue: " + queueName);

        ReceiveMessageResult result = new ReceiveMessageResult();

        if (isQueue2(queueName)) {
            readFromDummyQueue(result, dummySQS2);
        } else {
            readFromDummyQueue(result, dummySQS1);
        }
        return result;
    }

    @Override
    public void deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        String queueName = deleteMessageRequest.getQueueUrl();
        LOGGER.info(">>> Example AWS Dummy SQS Client deleteMessage(...) called for queue: " + queueName);
        if (isQueue2(queueName)) {
            dummySQS2.remove(0);
        } else {
            dummySQS1.remove(0);
        }
    }

    @Override
    public GetQueueUrlResult getQueueUrl(String nameOfQueue) {
        GetQueueUrlResult getQueueUrlResult = new GetQueueUrlResult();
        getQueueUrlResult.setQueueUrl(nameOfQueue);
        return getQueueUrlResult;
    }

    public static void addMessage(String messageBody, String nameOfQueue) {
        LOGGER.info(">>> Example AWS Dummy SQS Client addMessage(...) called with " + messageBody);
        Message msg = new Message();
        msg.setBody(messageBody);
        if (isQueue2(nameOfQueue)) {
            dummySQS2.add(0, msg);
        } else {
            dummySQS1.add(0, msg);
        }
    }

    private static boolean isQueue2(String queueName) {
        return queueName.contains("default-example-queue-2-name");
    }

    public static boolean isDummySQS1Empty() {
        LOGGER.info(">>> Example AWS Dummy SQS Client isDummySQS1Empty called. dummySQS1 queue has " + dummySQS1.size() + " elements.");
        return dummySQS1.isEmpty();
    }

    public static boolean isDummySQS2Empty() {
        LOGGER.info(">>> Example AWS Dummy SQS Client isDummySQS2Empty called. dummySQS2 queue has " + dummySQS2.size() + " elements.");
        return dummySQS2.isEmpty();
    }

    public static boolean areQueuesEmpty() {
        LOGGER.info(">>> Example AWS Dummy SQS Client areQueuesEmpty called. dummySQS1 has " + dummySQS1.size() + " elements.");
        LOGGER.info(">>> Example AWS Dummy SQS Client areQueuesEmpty called. dummySQS2 queue has " + dummySQS2.size() + " elements.");
        return dummySQS1.isEmpty() && dummySQS2.isEmpty();
    }

    private void readFromDummyQueue(ReceiveMessageResult receiveMessageResult, List<Message> queueToReadFrom) {
        List<Message> firstQueueMessage = new ArrayList<>();
        if (!queueToReadFrom.isEmpty()) {
            firstQueueMessage.add(queueToReadFrom.get(0));
            receiveMessageResult.setMessages(firstQueueMessage);
        }
    }
}

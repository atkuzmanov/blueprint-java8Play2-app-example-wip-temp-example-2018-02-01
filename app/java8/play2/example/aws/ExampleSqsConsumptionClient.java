package java8.play2.example.aws;

import java8.play2.example.aws.dummies.DummyExampleAWSSQSClient;
import java8.play2.example.utilities.ExampleEnvConfiguration;
import java8.play2.example.utilities.ExampleUtils;

import com.google.inject.Singleton;
import com.google.inject.Inject;

import java.util.List;
import java.util.Collections;

import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.AmazonClientException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Singleton
public class ExampleSqsConsumptionClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSqsConsumptionClient.class);

    private final ExampleAwsCredentialsConfigurator defaultAwsCredentialsConfigurator;

    private final AmazonSQSClient awsSQSClient;

    private static final String AWS_SQS_URL = "sqs.eu-west-1.amazonaws.com";

    private AmazonSQSClient createDefaultAWSSQSClient() {
        AmazonSQSClient amazonSQSClient;
        amazonSQSClient = new AmazonSQSClient(defaultAwsCredentialsConfigurator.awsCredentialsProvider, defaultAwsCredentialsConfigurator.awsClientConfiguration);
        amazonSQSClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        amazonSQSClient.setEndpoint(AWS_SQS_URL);
        return amazonSQSClient;
    }

    private AmazonSQSClient createDefaultDummyAWSSQSClient() {
        return new DummyExampleAWSSQSClient();
    }

    @Inject
    public ExampleSqsConsumptionClient(ExampleAwsCredentialsConfigurator defaultAwsCredentialsConfigurator) {
        String runningEnvironment = ExampleEnvConfiguration.systemRunningEnvironment();
        if (runningEnvironment.equals("management-jenkins") || runningEnvironment.equals("development")) {
            this.awsSQSClient = createDefaultDummyAWSSQSClient();
        } else {
            this.awsSQSClient = createDefaultAWSSQSClient();
        }
        this.defaultAwsCredentialsConfigurator = defaultAwsCredentialsConfigurator;
    }

    /**
     * @param maximumNumberOfMsgs
     * @param nameOfSqsQueue
     * @return a list of SQS messages, if permission to the SQS queue is denied, then return empty list.
     */
    public List<Message> readNumberOfSQSMsgs(int maximumNumberOfMsgs, String nameOfSqsQueue) {
        String sqsQueueUrl;
        try {
            sqsQueueUrl = awsSQSClient.getQueueUrl(this.buildQueueName(nameOfSqsQueue)).getQueueUrl();
        } catch (AmazonClientException e) {
            LOGGER.info("Problem encountered trying to load AWS credentials: ", e);
            return Collections.emptyList();
        } catch (Exception ex) {
            LOGGER.info("All permissions for " + nameOfSqsQueue + " SQS queue are revoked.", ex);
            return Collections.emptyList();
        }
        ReceiveMessageRequest request = new ReceiveMessageRequest(sqsQueueUrl);
        request.setMaxNumberOfMessages(maximumNumberOfMsgs);
        return awsSQSClient.receiveMessage(request).getMessages();
    }

    public void deleteMessage(Message message, String nameOfSqsQueue) {
        String sqsQueueUrl =  awsSQSClient.getQueueUrl(this.buildQueueName(nameOfSqsQueue)).getQueueUrl();
        DeleteMessageRequest req = new DeleteMessageRequest(sqsQueueUrl, message.getReceiptHandle());
        awsSQSClient.deleteMessage(req);
    }

    private String buildQueueName(String nameOfSqsQueue) {
        return ExampleUtils.exampleEnviroNameWithFallback(defaultAwsCredentialsConfigurator.getEnvironment()) + nameOfSqsQueue;
    }
}

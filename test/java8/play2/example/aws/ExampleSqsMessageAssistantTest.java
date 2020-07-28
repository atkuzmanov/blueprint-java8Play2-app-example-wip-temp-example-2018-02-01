package java8.play2.example.aws;

import com.amazonaws.services.sqs.model.Message;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExampleSqsMessageAssistantTest {

    @Mock
    ExampleSqsConsumptionClient exampleSqsConsumptionClient;

    private ExampleSqsMessageAssistant exampleSqsMessageAssistant;
    private List<Message> exampleListOfMsgs;
    private String defaultSQSname;

    public ExampleSqsMessageAssistantTest() {
        defaultSQSname = "default_example_sqs_name";
        exampleListOfMsgs = new ArrayList();
    }

    @Before
    public void before() {
        exampleSqsMessageAssistant = new ExampleSqsMessageAssistant(exampleSqsConsumptionClient);
    }

    @Test
    public void pcikMessagesFromSQS() {
        exampleListOfMsgs.add(new Message());

        when(exampleSqsConsumptionClient.readNumberOfSQSMsgs(1, defaultSQSname)).thenReturn(exampleListOfMsgs);

        List<Message> resultMsgs = exampleSqsMessageAssistant.defaultReadMessages(1, defaultSQSname);

        verify(exampleSqsConsumptionClient, times(1)).readNumberOfSQSMsgs(1, defaultSQSname);
        assert(resultMsgs.size() == 1);
    }

    @Test
    public void deleteCorrectNumberOfMessagesFromQueue() {
        exampleListOfMsgs.add(new Message());
        exampleListOfMsgs.add(new Message());

        doNothing().when(exampleSqsConsumptionClient).deleteMessage(isA(Message.class), isA(String.class));

        exampleSqsMessageAssistant.defaultDeleteMessages(exampleListOfMsgs, defaultSQSname);

        verify(exampleSqsConsumptionClient, times(2)).deleteMessage(isA(Message.class), isA(String.class));
    }
}

